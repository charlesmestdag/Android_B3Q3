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
import com.mestdag.gestionpointsetudiants.viewmodel.CourseListViewModel;
import com.mestdag.gestionpointsetudiants.model.Course;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import android.util.Log;

public class CourseListFragment extends Fragment {
    private String className;
    private long studentId; // Ajout de l'ID de l'étudiant
    private String studentName; // Ajout du nom de l'étudiant
    private AppDatabase database;
    private CourseListViewModel viewModel;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        database = AppDatabase.getInstance(getContext());
        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);
        courseList = new ArrayList<>();

        // Récupérer les arguments
        if (getArguments() != null) {
            className = getArguments().getString("className", "");
            studentId = getArguments().getLong("studentId", -1);
            studentName = getArguments().getString("studentName", "");

            TextView titleView = view.findViewById(R.id.tv_course_title);
            if (titleView != null) {
                String title = "Cours du bloc " + className;
                if (!studentName.isEmpty()) {
                    title += " - " + studentName;
                }
                titleView.setText(title);
            }
        }

        // Configuration RecyclerView
        recyclerView = view.findViewById(R.id.recycler_courses);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CourseAdapter(courseList);
            recyclerView.setAdapter(adapter);
        }

        // Bouton d'ajout
        Button btnAddCourse = view.findViewById(R.id.btn_add_course);
        if (btnAddCourse != null) {
            btnAddCourse.setOnClickListener(v -> showAddCourseDialog());
        }

        // ViewModel bindings
        viewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            courseList.clear();
            if (courses != null) courseList.addAll(courses);
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

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ajouter un Cours");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.setBackgroundColor(getResources().getColor(R.color.light_gray_background));

        final EditText nameInput = new EditText(getContext());
        nameInput.setHint("Nom du cours (ex: Mathématiques, Français...)");
        nameInput.setTextColor(getResources().getColor(R.color.white));
        nameInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        nameInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        nameInput.setPadding(12, 12, 12, 12);
        nameInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        nameInput.setFocusable(true);
        nameInput.setFocusableInTouchMode(true);
        nameInput.setClickable(true);
        layout.addView(nameInput);

        final EditText descInput = new EditText(getContext());
        descInput.setHint("Description (optionnel)");
        descInput.setTextColor(getResources().getColor(R.color.white));
        descInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        descInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        descInput.setPadding(12, 12, 12, 12);
        descInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        descInput.setFocusable(true);
        descInput.setFocusableInTouchMode(true);
        descInput.setClickable(true);
        layout.addView(descInput);

        builder.setView(layout);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String description = descInput.getText().toString().trim();

            // Validation du nom du cours
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer un nom de cours", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (name.length() < 2) {
                Toast.makeText(getContext(), "Le nom du cours doit contenir au moins 2 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si tout est valide, ajouter le cours via ViewModel
            viewModel.addCourse(name);
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
        nameInput.requestFocus();
    }

    // addCourse délégué au ViewModel

    // loadCourses délégué au ViewModel

    private void deleteCourse(Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Supprimer le cours");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer le cours '" + course.getName() + "' ?");

        builder.setPositiveButton("Supprimer", (dialog, which) -> {
            viewModel.deleteCourse(course);
            Toast.makeText(getContext(), "Cours supprimé!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Adapter pour les cours
    private class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
        private List<Course> courses;

        public CourseAdapter(List<Course> courses) {
            this.courses = courses;
        }

        @Override
        public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseViewHolder holder, int position) {
            Course course = courses.get(position);
            holder.tvCourseName.setText(course.getName());
            holder.tvCourseDesc.setText("Classe: " + course.getClassName());
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            TextView tvCourseName, tvCourseDesc;
            Button btnDelete;
            LinearLayout courseInfoLayout;

            CourseViewHolder(View itemView) {
                super(itemView);
                tvCourseName = itemView.findViewById(R.id.tv_course_name);
                tvCourseDesc = itemView.findViewById(R.id.tv_course_desc);
                btnDelete = itemView.findViewById(R.id.btn_delete_course);
                courseInfoLayout = itemView.findViewById(R.id.course_info_layout);

                // Clic sur le cours pour voir les évaluations
                if (courseInfoLayout != null) {
                    courseInfoLayout.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Course clickedCourse = courses.get(position);

                            Bundle args = new Bundle();
                            args.putLong("courseId", clickedCourse.getId());
                            args.putString("courseName", clickedCourse.getName());
                            args.putLong("studentId", studentId);
                            args.putString("studentName", studentName);

                            try {
                                // Utiliser une approche plus sûre pour la navigation
                                if (getFragmentManager() != null) {
                                    Navigation.findNavController(requireView()).navigate(R.id.action_course_list_to_evaluation_list, args);
                                    Toast.makeText(getContext(), "Ouverture du cours " + clickedCourse.getName(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("CourseListFragment", "Erreur lors de la navigation", e);
                                Toast.makeText(getContext(), "Erreur lors de la navigation", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Bouton de suppression
                if (btnDelete != null) {
                    btnDelete.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Course courseToDelete = courses.get(position);
                            viewModel.deleteCourse(courseToDelete);
                        }
                    });
                }
            }
        }
    }
}
