package com.mestdag.gestionpointsetudiants.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import com.mestdag.gestionpointsetudiants.utils.GradeUtils;
import com.mestdag.gestionpointsetudiants.utils.WeightedGradeCalculator;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import androidx.navigation.Navigation;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Course;
import android.util.Log;
import com.mestdag.gestionpointsetudiants.viewmodel.NoteEntryViewModel;

public class NoteEntryFragment extends Fragment {
    private long evaluationId;
    private String evaluationName;
    private double evaluationMaxPoints;
    private AppDatabase database;
    private NoteEntryViewModel viewModel;
    private RecyclerView recyclerView;
    private StudentGradeAdapter adapter;
    private List<StudentGradeInfo> studentGradeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_entry, container, false);

        database = AppDatabase.getInstance(getContext());
        viewModel = new ViewModelProvider(this).get(NoteEntryViewModel.class);
        studentGradeList = new ArrayList<>();

        // Récupérer les arguments
        if (getArguments() != null) {
            evaluationId = getArguments().getLong("evaluationId", -1);
            evaluationName = getArguments().getString("evaluationName", "");
            evaluationMaxPoints = getArguments().getDouble("evaluationMaxPoints", 20.0);

            TextView titleView = view.findViewById(R.id.tv_note_title);
            if (titleView != null) {
                String title = "Notes - " + evaluationName + " (/" + (int)evaluationMaxPoints + ")";
                titleView.setText(title);
            }
        }

        // Configuration RecyclerView
        recyclerView = view.findViewById(R.id.recycler_student_grades);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new StudentGradeAdapter(studentGradeList);
            recyclerView.setAdapter(adapter);
        }

        // ViewModel bindings
        viewModel.getStudents().observe(getViewLifecycleOwner(), infos -> {
            studentGradeList.clear();
            if (infos != null) {
                // Mapper le modèle VM vers le modèle local utilisé par l'adapter
                for (com.mestdag.gestionpointsetudiants.viewmodel.NoteEntryViewModel.StudentGradeInfo sgi : infos) {
                    StudentGradeInfo info = new StudentGradeInfo();
                    info.student = sgi.student;
                    info.currentGrade = sgi.currentGrade;
                    info.isForced = sgi.isForced;
                    info.forcedGradeReason = sgi.forcedGradeReason;
                    studentGradeList.add(info);
                }
            }
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                updateStatistics(stats.average, stats.totalStudents, stats.gradedStudents);
            }
        });

        if (getArguments() != null) {
            viewModel.init(evaluationId);
        }
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

    private void loadStudentGrades() {
        // La logique de chargement est déléguée au ViewModel
        viewModel.load();
    }
    
    private void updateStatistics(double average, int totalStudents, int gradedStudents) {
        // Mettre à jour le titre avec les statistiques
        TextView titleView = getView().findViewById(R.id.tv_note_title);
        if (titleView != null) {
            String title = "Notes - " + evaluationName + " (/" + (int)evaluationMaxPoints + ")";
            title += "\nMoyenne pondérée: " + GradeUtils.formatGrade(average);
            titleView.setText(title);
        }
    }

    private void showGradeDialog(StudentGradeInfo studentInfo) {
        // Vérifier si c'est une évaluation principale avec des sous-évaluations
        Evaluation evaluation = database.evaluationDao().getEvaluationById(evaluationId);
        boolean isMainEvaluationWithSubs = false;
        
        if (evaluation != null && evaluation.getParentId() == 0) {
            isMainEvaluationWithSubs = database.evaluationDao().hasSubEvaluations(evaluationId);
        }
        
        // Si c'est une évaluation principale avec des sous-évaluations, ne permettre que le forçage
        if (isMainEvaluationWithSubs) {
            showForceGradeDialog(studentInfo);
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Note pour " + studentInfo.student.getFirstName() + " " + studentInfo.student.getLastName());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.setBackgroundColor(getResources().getColor(R.color.light_gray_background));

        final EditText gradeInput = new EditText(getContext());
        gradeInput.setHint("entrez une note max (" + (int)evaluationMaxPoints + " max défini pour cette sous eval)");
        gradeInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        gradeInput.setTextColor(getResources().getColor(R.color.white));
        gradeInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        gradeInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        gradeInput.setPadding(12, 12, 12, 12);
        gradeInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        gradeInput.setFocusable(true);
        gradeInput.setFocusableInTouchMode(true);
        gradeInput.setClickable(true);
        
        if (studentInfo.currentGrade >= 0) {
            gradeInput.setText(GradeUtils.formatGrade(studentInfo.currentGrade));
        }
        
        layout.addView(gradeInput);

        builder.setView(layout);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String gradeStr = gradeInput.getText().toString().trim();
            
            if (gradeStr.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer une note", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double grade = Double.parseDouble(gradeStr);
                
                // Validation de la note
                if (grade < 0 || grade > evaluationMaxPoints) {
                    Toast.makeText(getContext(), "La note doit être entre 0 et " + (int)evaluationMaxPoints, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Validation du format (multiples de 0.5)
                if (!GradeUtils.isValidGradeFormat(gradeStr)) {
                    Toast.makeText(getContext(), "Format invalide. Utilisez des multiples de 0.5 (ex: 15, 15.5, 16)", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Si tout est valide, sauvegarder la note
                saveGrade(studentInfo, grade);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Format de note invalide. Entrez un nombre valide", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        
        // Ajouter le bouton "Forcer la moyenne" seulement pour les évaluations principales
        if (studentInfo.currentGrade >= 0 && evaluation != null && evaluation.getParentId() == 0) {
            builder.setNeutralButton("Forcer la moyenne", (dialog, which) -> {
                showForceGradeDialog(studentInfo);
            });
        }
        
        AlertDialog dialog = builder.create();
        
        // Appliquer les styles aux boutons
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.blue_button));
            
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_button));
            
            // Appliquer le style au bouton neutre seulement s'il existe (évaluations principales)
            if (studentInfo.currentGrade >= 0 && evaluation != null && evaluation.getParentId() == 0) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(getResources().getColor(R.color.orange_button));
            }
        });
        
        dialog.show();
        
        // Donner le focus au champ de saisie
        gradeInput.requestFocus();
    }

    private void showForceGradeDialog(StudentGradeInfo studentInfo) {
        // Vérifier si c'est une évaluation principale avec des sous-évaluations
        Evaluation evaluation = database.evaluationDao().getEvaluationById(evaluationId);
        boolean isMainEvaluationWithSubs = false;
        
        if (evaluation != null && evaluation.getParentId() == 0) {
            isMainEvaluationWithSubs = database.evaluationDao().hasSubEvaluations(evaluationId);
        }
        
        String dialogTitle = "Forcer la moyenne pour " + studentInfo.student.getFirstName() + " " + studentInfo.student.getLastName();
        if (isMainEvaluationWithSubs) {
            dialogTitle = "Forcer la note (calculée automatiquement) pour " + studentInfo.student.getFirstName() + " " + studentInfo.student.getLastName();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(dialogTitle);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.setBackgroundColor(getResources().getColor(R.color.light_gray_background));

        final EditText forcedGradeInput = new EditText(getContext());
        forcedGradeInput.setHint("entrez une note max :" + (int)evaluationMaxPoints + " (défini pour cette sous eval)");
        forcedGradeInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        forcedGradeInput.setTextColor(getResources().getColor(R.color.white));
        forcedGradeInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        forcedGradeInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        forcedGradeInput.setPadding(12, 12, 12, 12);
        forcedGradeInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        forcedGradeInput.setFocusable(true);
        forcedGradeInput.setFocusableInTouchMode(true);
        forcedGradeInput.setClickable(true);
        
        if (studentInfo.currentGrade >= 0) {
            forcedGradeInput.setText(GradeUtils.formatGrade(studentInfo.currentGrade));
        }
        
        layout.addView(forcedGradeInput);

        // Ajouter un texte explicatif si c'est une évaluation principale avec des sous-évaluations
        if (isMainEvaluationWithSubs) {
            TextView infoText = new TextView(getContext());
            infoText.setText("Note: Cette note est normalement calculée automatiquement à partir des sous-évaluations. Le forçage remplacera cette valeur.");
            infoText.setTextSize(12);
            infoText.setTextColor(getResources().getColor(R.color.blue_accent));
            infoText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            infoText.setPadding(0, 8, 0, 8);
            layout.addView(infoText);
        }

        final EditText reasonInput = new EditText(getContext());
        reasonInput.setHint("Raison du forçage (optionnel)");
        reasonInput.setTextColor(getResources().getColor(R.color.white));
        reasonInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        reasonInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        reasonInput.setPadding(12, 12, 12, 12);
        reasonInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        reasonInput.setFocusable(true);
        reasonInput.setFocusableInTouchMode(true);
        reasonInput.setClickable(true);
        
        if (!studentInfo.forcedGradeReason.isEmpty()) {
            reasonInput.setText(studentInfo.forcedGradeReason);
        }
        
        layout.addView(reasonInput);

        builder.setView(layout);

        builder.setPositiveButton("Forcer", (dialog, which) -> {
            String gradeStr = forcedGradeInput.getText().toString().trim();
            String reason = reasonInput.getText().toString().trim();
            
            if (gradeStr.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer une note", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double grade = Double.parseDouble(gradeStr);
                
                // Validation de la note
                if (grade < 0 || grade > evaluationMaxPoints) {
                    Toast.makeText(getContext(), "La note doit être entre 0 et " + (int)evaluationMaxPoints, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Validation du format (multiples de 0.5)
                if (!GradeUtils.isValidGradeFormat(gradeStr)) {
                    Toast.makeText(getContext(), "Format invalide. Utilisez des multiples de 0.5 (ex: 15, 15.5, 16)", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Forcer la note
                forceGrade(studentInfo, grade, reason);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Format de note invalide. Entrez un nombre valide", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        
        if (studentInfo.isForced) {
            builder.setNeutralButton("Supprimer le forçage", (dialog, which) -> {
                removeForcedGrade(studentInfo);
            });
        }
        
        AlertDialog dialog = builder.create();
        
        // Appliquer les styles aux boutons
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.blue_button));
            
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_button));
            
            if (studentInfo.isForced) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(getResources().getColor(R.color.orange_button));
            }
        });
        
        dialog.show();
        
        // Donner le focus au champ de saisie
        forcedGradeInput.requestFocus();
    }

    private void forceGrade(StudentGradeInfo studentInfo, double grade, String reason) {
        viewModel.forceGrade(studentInfo.student.getId(), grade, reason);
        Toast.makeText(getContext(), "Note forcée: " + GradeUtils.formatGrade(grade), Toast.LENGTH_SHORT).show();
    }

    private void removeForcedGrade(StudentGradeInfo studentInfo) {
        viewModel.removeForcedGrade(studentInfo.student.getId());
        Toast.makeText(getContext(), "Forçage supprimé", Toast.LENGTH_SHORT).show();
    }

    private void saveGrade(StudentGradeInfo studentInfo, double grade) {
        viewModel.saveGrade(studentInfo.student.getId(), grade);
        Toast.makeText(getContext(), "Note enregistrée: " + GradeUtils.formatGrade(grade), Toast.LENGTH_SHORT).show();
    }

    private void deleteGrade(StudentGradeInfo studentInfo) {
        new Thread(() -> {
            try {
                database.noteDao().deleteByEvaluationAndStudent(evaluationId, studentInfo.student.getId());

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Note supprimée", Toast.LENGTH_SHORT).show();
                    studentInfo.currentGrade = -1;
                    adapter.notifyDataSetChanged();
                    
                    // Recharger les statistiques
                    reloadStatistics();
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Erreur lors de la suppression: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void reloadStatistics() {
        viewModel.reloadStatistics();
    }

    // Classe pour associer étudiant et note
    private static class StudentGradeInfo {
        Student student;
        double currentGrade = -1; // -1 = pas de note
        boolean isForced = false; // Indique si la note est forcée
        String forcedGradeReason = ""; // Raison du forçage
    }

    // Adapter pour les notes des étudiants
    private class StudentGradeAdapter extends RecyclerView.Adapter<StudentGradeAdapter.StudentGradeViewHolder> {
        private List<StudentGradeInfo> studentGrades;

        public StudentGradeAdapter(List<StudentGradeInfo> studentGrades) {
            this.studentGrades = studentGrades;
        }

        @Override
        public StudentGradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_grade, parent, false);
            return new StudentGradeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StudentGradeViewHolder holder, int position) {
            StudentGradeInfo info = studentGrades.get(position);
            
            // Afficher le nom avec la raison du forçage si applicable
                          String studentName = info.student.getFirstName() + " " + info.student.getLastName();
            if (info.isForced && !info.forcedGradeReason.isEmpty()) {
                studentName += " (" + info.forcedGradeReason + ")";
            }
            holder.tvStudentName.setText(studentName);
            
                          holder.tvStudentMatricule.setText("(" + info.student.getClassName() + ")");
            
            if (info.currentGrade >= 0) {
                String gradeText = GradeUtils.formatGrade(info.currentGrade) + "/" + (int)evaluationMaxPoints;
                if (info.isForced) {
                    gradeText = "[FORCÉE] " + gradeText;
                    holder.tvCurrentGrade.setTextColor(getResources().getColor(R.color.orange_accent));
                } else {
                    // Vérifier si c'est une évaluation principale avec des sous-évaluations
                    Evaluation evaluation = database.evaluationDao().getEvaluationById(evaluationId);
                    if (evaluation != null && evaluation.getParentId() == 0) {
                        boolean hasSubEvaluations = database.evaluationDao().hasSubEvaluations(evaluationId);
                        if (hasSubEvaluations) {
                            gradeText = "[AUTO] " + gradeText;
                            holder.tvCurrentGrade.setTextColor(getResources().getColor(R.color.blue_accent));
                        } else {
                            holder.tvCurrentGrade.setTextColor(getResources().getColor(android.R.color.black));
                        }
                    } else {
                        holder.tvCurrentGrade.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
                holder.tvCurrentGrade.setText(gradeText);
            } else {
                holder.tvCurrentGrade.setText("Pas de note");
                holder.tvCurrentGrade.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }

        @Override
        public int getItemCount() {
            return studentGrades.size();
        }

        class StudentGradeViewHolder extends RecyclerView.ViewHolder {
            TextView tvStudentName, tvStudentMatricule, tvCurrentGrade;
            LinearLayout studentLayout;

            StudentGradeViewHolder(View itemView) {
                super(itemView);
                tvStudentName = itemView.findViewById(R.id.tv_student_name_grade);
                tvStudentMatricule = itemView.findViewById(R.id.tv_student_matricule_grade);
                tvCurrentGrade = itemView.findViewById(R.id.tv_current_grade);
                studentLayout = itemView.findViewById(R.id.student_grade_layout);

                studentLayout.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        StudentGradeInfo studentInfo = studentGrades.get(position);
                        showGradeDialog(studentInfo);
                    }
                });
            }
        }
    }
}