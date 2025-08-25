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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mestdag.gestionpointsetudiants.R;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.viewmodel.StudentListViewModel;
import com.mestdag.gestionpointsetudiants.model.Student;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import android.util.Log;

public class StudentListFragment extends Fragment {
    private long classId;
    private String className;
    private AppDatabase database;
    private StudentListViewModel viewModel;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        
        database = AppDatabase.getInstance(getContext());
        viewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
        studentList = new ArrayList<>();
        
        // Récupérer les arguments passés depuis ClassListFragment
        if (getArguments() != null) {
            classId = getArguments().getLong("classId", -1);
            className = getArguments().getString("className", "");
            
            // Mettre à jour le titre pour afficher la classe sélectionnée
            TextView titleView = view.findViewById(R.id.tv_student_title);
            if (titleView != null) {
                titleView.setText("Étudiants du bloc " + className);
            }
        }
        
        // Configuration de la RecyclerView
        recyclerView = view.findViewById(R.id.recycler_students);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new StudentAdapter(studentList);
            recyclerView.setAdapter(adapter);
        }
        
        // Bouton d'ajout d'étudiant
        Button btnAddStudent = view.findViewById(R.id.btn_add_student);
        if (btnAddStudent != null) {
            btnAddStudent.setOnClickListener(v -> showAddStudentDialog());
        }
        
        // ViewModel bindings
        viewModel.getStudents().observe(getViewLifecycleOwner(), students -> {
            studentList.clear();
            if (students != null) studentList.addAll(students);
            if (adapter != null) adapter.notifyDataSetChanged();
        });
        viewModel.init(className);
        
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
    
    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ajouter un Étudiant");
        
        // Créer un layout pour le formulaire
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.setBackgroundColor(getResources().getColor(R.color.light_gray_background));
        
        final EditText matriculeInput = new EditText(getContext());
        matriculeInput.setHint("matricule format laxxxxxx");
        matriculeInput.setTextColor(getResources().getColor(R.color.white));
        matriculeInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        matriculeInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        matriculeInput.setPadding(12, 12, 12, 12);
        matriculeInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        matriculeInput.setFocusable(true);
        matriculeInput.setFocusableInTouchMode(true);
        matriculeInput.setClickable(true);
        layout.addView(matriculeInput);
        
        final EditText nomInput = new EditText(getContext());
        nomInput.setHint("Nom");
        nomInput.setTextColor(getResources().getColor(R.color.white));
        nomInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        nomInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        nomInput.setPadding(12, 12, 12, 12);
        nomInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        nomInput.setFocusable(true);
        nomInput.setFocusableInTouchMode(true);
        nomInput.setClickable(true);
        layout.addView(nomInput);
        
        final EditText prenomInput = new EditText(getContext());
        prenomInput.setHint("Prénom");
        prenomInput.setTextColor(getResources().getColor(R.color.white));
        prenomInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        prenomInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        prenomInput.setPadding(12, 12, 12, 12);
        prenomInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        prenomInput.setFocusable(true);
        prenomInput.setFocusableInTouchMode(true);
        prenomInput.setClickable(true);
        layout.addView(prenomInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String matricule = matriculeInput.getText().toString().trim();
            String nom = nomInput.getText().toString().trim();
            String prenom = prenomInput.getText().toString().trim();
            
            // Validation du matricule (exactement 6 chiffres)
            if (matricule.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer un matricule", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validation du matricule supprimée car nous n'utilisons plus ce champ
            
            // Validation du nom et prénom
            if (nom.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (prenom.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer un prénom", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Si tout est valide, ajouter l'étudiant via ViewModel
            viewModel.addStudent(nom, prenom);
        });
        
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        
        // Appliquer les styles aux boutons
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.blue_button));
            
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_button));
        });
        
        dialog.show();
        
        // Donner le focus au premier champ de saisie
        matriculeInput.requestFocus();
    }
    
    // addStudent délégué au ViewModel
    
    // loadStudents délégué au ViewModel
    
    private void deleteStudent(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Supprimer l'étudiant");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer " + student.getFirstName() + " " + student.getLastName() + " ?");
        
        builder.setPositiveButton("Supprimer", (dialog, which) -> {
            viewModel.deleteStudent(student);
            Toast.makeText(getContext(), "Étudiant supprimé!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    // Adapter pour la RecyclerView des étudiants
    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private List<Student> students;
        
        public StudentAdapter(List<Student> students) {
            this.students = students;
        }
        
        @Override
        public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
            return new StudentViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(StudentViewHolder holder, int position) {
            Student student = students.get(position);
            holder.tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
            holder.tvStudentMatricule.setText("Classe: " + student.getClassName());
        }
        
        @Override
        public int getItemCount() {
            return students.size();
        }
        
        class StudentViewHolder extends RecyclerView.ViewHolder {
            TextView tvStudentName, tvStudentMatricule;
            Button btnDelete;
            LinearLayout studentInfoLayout;
            
            StudentViewHolder(View itemView) {
                super(itemView);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvStudentMatricule = itemView.findViewById(R.id.tv_student_matricule);
                btnDelete = itemView.findViewById(R.id.btn_delete_student);
                studentInfoLayout = itemView.findViewById(R.id.student_info_layout);
                
                // Clic sur l'étudiant pour voir ses notes
                if (studentInfoLayout != null) {
                    studentInfoLayout.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Student clickedStudent = students.get(position);
                            
                            // Naviguer vers les cours du bloc pour cet étudiant
                            Bundle args = new Bundle();
                            args.putLong("classId", classId);
                            args.putString("className", className);
                            args.putLong("studentId", clickedStudent.getId());
                            args.putString("studentName", clickedStudent.getFirstName() + " " + clickedStudent.getLastName());
                            
                            Navigation.findNavController(v).navigate(R.id.action_student_list_to_course_list, args);
                            
                            Toast.makeText(getContext(), "Cours pour " + clickedStudent.getFirstName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                // Bouton de suppression
                if (btnDelete != null) {
                    btnDelete.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Student studentToDelete = students.get(position);
                            viewModel.deleteStudent(studentToDelete);
                        }
                    });
                }
            }
        }
    }
}