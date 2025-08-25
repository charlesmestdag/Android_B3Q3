package com.mestdag.gestionpointsetudiants.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mestdag.gestionpointsetudiants.R;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.viewmodel.ClassListViewModel;
import com.mestdag.gestionpointsetudiants.model.ClassEntity;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import android.util.Log;

public class ClassListFragment extends Fragment {
    private AppDatabase database;
    private ClassListViewModel viewModel;
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private List<ClassEntity> classList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_list, container, false);
        
        database = AppDatabase.getInstance(getContext());
        viewModel = new ViewModelProvider(this).get(ClassListViewModel.class);
        classList = new ArrayList<>();
        
        Button btnAddClass = view.findViewById(R.id.btn_add_class);
        btnAddClass.setOnClickListener(v -> showAddClassDialog());
        
        recyclerView = view.findViewById(R.id.recycler_classes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(classList);
        recyclerView.setAdapter(adapter);
        
        // ViewModel bindings
        viewModel.getClasses().observe(getViewLifecycleOwner(), classes -> {
            classList.clear();
            if (classes != null) classList.addAll(classes);
            adapter.notifyDataSetChanged();
        });
        viewModel.load();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Logique pour gérer le retour
                Navigation.findNavController(view).navigateUp();
            }
        });
    }
    
    private void showAddClassDialog() {
        // Vérifier quelles classes sont déjà ajoutées
        new Thread(() -> {
            try {
                List<ClassEntity> existingClasses = database.classDao().getAllClasses();
                String[] allClassOptions = {"BA1", "BA2", "BA3", "MA1", "MA2"};
                
                // Filtrer les options disponibles (non encore ajoutées)
                List<String> availableOptions = new ArrayList<>();
                for (String option : allClassOptions) {
                    boolean exists = false;
                    for (ClassEntity existing : existingClasses) {
                        if (existing.getName().equals(option)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        availableOptions.add(option);
                    }
                }
                
                getActivity().runOnUiThread(() -> {
                    if (availableOptions.isEmpty()) {
                        Toast.makeText(getContext(), "Toutes les classes ont déjà été ajoutées !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Sélectionner une Classe");
                    
                    String[] availableArray = availableOptions.toArray(new String[0]);
                    
                    builder.setItems(availableArray, (dialog, which) -> {
                        String selectedClass = availableArray[which];
                        viewModel.addClass(selectedClass);
                    });
                    
                    builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
                    
                    AlertDialog dialog = builder.create();
                    
                    // Appliquer les styles aux boutons
                    dialog.setOnShowListener(dialogInterface -> {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_button));
                    });
                    
                    dialog.show();
                });
                
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    // loadClasses délégué au ViewModel
    
    // addClass délégué au ViewModel
    
    private void deleteClass(ClassEntity classEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Supprimer la classe");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer la classe '" + classEntity.getName() + "' ?");
        
        builder.setPositiveButton("Supprimer", (dialog, which) -> {
            new Thread(() -> {
                try {
                    viewModel.deleteClass(classEntity);
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Classe '" + classEntity.getName() + "' supprimée!", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Erreur lors de la suppression: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
        
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        
        // Appliquer les styles aux boutons
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red_button));
            
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.blue_button));
        });
        
        dialog.show();
    }
    
    // Adapter pour la RecyclerView
    private class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
        private List<ClassEntity> classes;
        
        public ClassAdapter(List<ClassEntity> classes) {
            this.classes = classes;
        }
        
        @Override
        public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
            return new ClassViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ClassViewHolder holder, int position) {
            ClassEntity classEntity = classes.get(position);
            holder.tvClassName.setText(classEntity.getName());
            holder.tvClassId.setText("Classe: " + classEntity.getName());
        }
        
        @Override
        public int getItemCount() {
            return classes.size();
        }
        
        class ClassViewHolder extends RecyclerView.ViewHolder {
            TextView tvClassName, tvClassId;
            Button btnDelete;
            LinearLayout classInfoLayout;
            
            ClassViewHolder(View itemView) {
                super(itemView);
                tvClassName = itemView.findViewById(R.id.tv_class_name);
                tvClassId = itemView.findViewById(R.id.tv_class_id);
                btnDelete = itemView.findViewById(R.id.btn_delete_class);
                classInfoLayout = itemView.findViewById(R.id.class_info_layout);
                
                // Comportement de clic sur la zone principale (navigation)
                classInfoLayout.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ClassEntity clickedClass = classes.get(position);
                        
                        // Naviguer vers la liste des étudiants avec les informations de la classe
                        Bundle args = new Bundle();
                        args.putString("className", clickedClass.getName());
                        
                        Navigation.findNavController(v).navigate(R.id.action_class_list_to_student_list, args);
                        
                        Toast.makeText(v.getContext(), "Ouverture de la classe " + clickedClass.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                // Comportement de clic sur le bouton supprimer
                btnDelete.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ClassEntity classToDelete = classes.get(position);
                        deleteClass(classToDelete);
                    }
                });
            }
        }
    }
}