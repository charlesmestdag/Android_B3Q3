package com.mestdag.gestionpointsetudiants.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.utils.EvaluationFactory;
import com.mestdag.gestionpointsetudiants.viewmodel.EvaluationViewModel;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

public class EvaluationListFragment extends Fragment {
    private long courseId;
    private String courseName;
    private long studentId;
    private String studentName;
    private EvaluationViewModel viewModel;
    private RecyclerView recyclerView;
    private EvaluationAdapter adapter;
    private TextView tvStudentAvg, tvClassAvg;
    private List<Evaluation> evaluationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation_list, container, false);

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);
        viewModel.setDatabase(AppDatabase.getInstance(getContext()));
        
        evaluationList = new ArrayList<>();

        // Récupérer les arguments
        if (getArguments() != null) {
            courseId = getArguments().getLong("courseId", -1);
            courseName = getArguments().getString("courseName", "");
            studentId = getArguments().getLong("studentId", -1);
            studentName = getArguments().getString("studentName", "");

            TextView titleView = view.findViewById(R.id.tv_evaluation_title);
            if (titleView != null) {
                String title = "Évaluations - " + courseName;
                if (!studentName.isEmpty()) {
                    title += " - " + studentName;
                }
                titleView.setText(title);
            }
            
            // Configurer le ViewModel avec l'ID du cours
            viewModel.setCourseId(courseId);
        } else {
            Toast.makeText(getContext(), "Erreur: Informations du cours manquantes", Toast.LENGTH_SHORT).show();
        }

        // Configuration RecyclerView
        recyclerView = view.findViewById(R.id.recycler_evaluations);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new EvaluationAdapter(evaluationList);
            recyclerView.setAdapter(adapter);
        }

        // Averages UI
        tvStudentAvg = view.findViewById(R.id.tv_student_course_average);
        tvClassAvg = view.findViewById(R.id.tv_class_course_average);

        // Bouton d'ajout
        Button btnAddEvaluation = view.findViewById(R.id.btn_add_evaluation);
        if (btnAddEvaluation != null) {
            btnAddEvaluation.setOnClickListener(v -> showAddEvaluationDialog(0));
        }

        // Observer les données du ViewModel
        observeViewModel();
        if (studentId != -1) {
            viewModel.computeCourseAverages(studentId);
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

    private void showAddEvaluationDialog(long parentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(parentId == 0 ? "Ajouter une Évaluation" : "Ajouter une Sous-Évaluation");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.setBackgroundColor(getResources().getColor(R.color.light_gray_background));

        final EditText nameInput = new EditText(getContext());
        nameInput.setHint("Nom de l'évaluation (ex: Examen 1)");
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

        final EditText pointsInput = new EditText(getContext());
        
        // Pour les évaluations principales, fixer à 20 et rendre non-éditable
        if (parentId == 0) {
            pointsInput.setText("20");
            pointsInput.setEnabled(false);
            pointsInput.setHint("Points maximum (fixé à 20 pour les évaluations principales)");
            pointsInput.setTextColor(getResources().getColor(R.color.gray_medium));
        } else {
            // Pour les sous-évaluations, champ éditable
            pointsInput.setHint("Points maximum (ex: 10)");
            pointsInput.setEnabled(true);
            pointsInput.setTextColor(getResources().getColor(R.color.white));
        }
        
        pointsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        pointsInput.setHintTextColor(getResources().getColor(R.color.gray_medium));
        pointsInput.setBackground(getResources().getDrawable(R.drawable.card_background));
        pointsInput.setPadding(12, 12, 12, 12);
        pointsInput.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        pointsInput.setFocusable(parentId != 0); // Focusable seulement pour les sous-évaluations
        pointsInput.setFocusableInTouchMode(parentId != 0);
        pointsInput.setClickable(parentId != 0);
        layout.addView(pointsInput);

        builder.setView(layout);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String pointsStr = pointsInput.getText().toString().trim();

            if (name.isEmpty() || pointsStr.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double points = Double.parseDouble(pointsStr);
                    if (points <= 0) {
                        Toast.makeText(getContext(), "Les points doivent être positifs", Toast.LENGTH_SHORT).show();
                    } else {
                        if (parentId == 0) {
                            viewModel.addMainEvaluation(name);
                        } else {
                            viewModel.addSubEvaluation(name, points, parentId);
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Format de points invalide", Toast.LENGTH_SHORT).show();
                }
            }
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
        
        // Donner le focus au champ nom pour les évaluations principales, au champ points pour les sous-évaluations
        if (parentId == 0) {
            nameInput.requestFocus();
        } else {
            pointsInput.requestFocus();
        }
    }

    // addEvaluation délégué au ViewModel

    // loadEvaluations délégué au ViewModel (observer via observeViewModel)

    private void observeViewModel() {
        // Observer les évaluations
        viewModel.getEvaluations().observe(getViewLifecycleOwner(), evaluations -> {
            if (evaluations != null) {
                evaluationList.clear();
                evaluationList.addAll(evaluations);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Observer les erreurs
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observer l'état de chargement
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Ici on pourrait afficher un indicateur de chargement
        });

        viewModel.getStudentCourseAverage().observe(getViewLifecycleOwner(), avg -> {
            if (tvStudentAvg != null) tvStudentAvg.setText("Moyenne élève: " + String.format(java.util.Locale.getDefault(), "%.2f", avg));
        });

        viewModel.getClassCourseAverage().observe(getViewLifecycleOwner(), avg -> {
            if (tvClassAvg != null) tvClassAvg.setText("Moyenne classe: " + String.format(java.util.Locale.getDefault(), "%.2f", avg));
        });
    }

    // Adapter pour les évaluations
    private class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationViewHolder> {
        private List<Evaluation> evaluations;

        public EvaluationAdapter(List<Evaluation> evaluations) {
            this.evaluations = evaluations;
        }

        @Override
        public EvaluationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evaluation, parent, false);
            return new EvaluationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EvaluationViewHolder holder, int position) {
            Evaluation evaluation = evaluations.get(position);
            
            // Utiliser les méthodes polymorphiques
            holder.tvEvaluationName.setText(evaluation.getDisplayName());
            holder.tvEvaluationPoints.setText("/" + evaluation.calculateTotalPoints() + " pts");
            
            // Utiliser le polymorphisme pour déterminer le comportement
            if (evaluation.getParentId() != 0) {
                // C'est une sous-évaluation
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.card_background));
                // Masquer le bouton "Sous-Éval" car les sous-évaluations ne peuvent pas avoir de sous-sous-évaluations
                holder.btnAddSub.setVisibility(View.GONE);
            } else {
                // C'est une évaluation principale
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.card_background));
                // Afficher le bouton "Sous-Éval" seulement si l'évaluation peut avoir des sous-évaluations
                holder.btnAddSub.setVisibility(evaluation.canHaveSubEvaluations() ? View.VISIBLE : View.GONE);
            }
            
            // Améliorer la visibilité des points
            holder.tvEvaluationPoints.setTextColor(getResources().getColor(R.color.orange_accent));
        }

        @Override
        public int getItemCount() {
            return evaluations.size();
        }

        class EvaluationViewHolder extends RecyclerView.ViewHolder {
            TextView tvEvaluationName, tvEvaluationPoints;
            Button btnAddSub, btnSeeNotes;
            LinearLayout evaluationInfoLayout;

            EvaluationViewHolder(View itemView) {
                super(itemView);
                tvEvaluationName = itemView.findViewById(R.id.tv_evaluation_name);
                tvEvaluationPoints = itemView.findViewById(R.id.tv_evaluation_points);
                btnAddSub = itemView.findViewById(R.id.btn_add_sub_evaluation);
                btnSeeNotes = itemView.findViewById(R.id.btn_see_notes);
                evaluationInfoLayout = itemView.findViewById(R.id.evaluation_info_layout);

                // Bouton ajouter sous-évaluation
                if (btnAddSub != null) {
                    btnAddSub.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Evaluation evaluation = evaluations.get(position);
                            // Utiliser le polymorphisme pour vérifier si on peut ajouter des sous-évaluations
                            if (evaluation.canHaveSubEvaluations()) {
                                showAddEvaluationDialog(evaluation.getId());
                            } else {
                                Toast.makeText(getContext(), "Impossible d'ajouter une sous-évaluation à cette évaluation", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Bouton voir les notes
                if (btnSeeNotes != null) {
                    btnSeeNotes.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Evaluation evaluation = evaluations.get(position);

                            Bundle args = new Bundle();
                            args.putLong("evaluationId", evaluation.getId());
                            args.putString("evaluationName", evaluation.getName());
                            args.putDouble("evaluationMaxPoints", evaluation.getPointsMax());
                            args.putLong("studentId", studentId);
                            args.putString("studentName", studentName);

                            Navigation.findNavController(v).navigate(R.id.action_evaluation_list_to_note_entry, args);

                            Toast.makeText(getContext(), "Notes pour " + evaluation.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}