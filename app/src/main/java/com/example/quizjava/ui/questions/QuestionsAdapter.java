package com.example.quizjava.ui.questions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.R;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionModel> questionsList;

    public QuestionsAdapter(List<QuestionModel> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_item_layout, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(i);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView ques;
        private Button optionA,optionB,optionC,optionD, prevSelectedBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.tv_question);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);

            prevSelectedBtn = null;
        }

        private void setData(final int pos){
            ques.setText(questionsList.get(pos).getQuestion());
            optionA.setText(questionsList.get(pos).getOptionA());
            optionB.setText(questionsList.get(pos).getOptionB());
            optionC.setText(questionsList.get(pos).getOptionC());
            optionD.setText(questionsList.get(pos).getOptionD());

            setOption(optionA, 1, pos);
            setOption(optionB, 2, pos);
            setOption(optionC, 3, pos);
            setOption(optionD, 4, pos);

            optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionA, 1, pos);
                }
            });

            optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionB, 2, pos);

                }
            });

            optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionC, 3, pos);

                }
            });

            optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(optionD, 4, pos);

                }
            });

        }
        private void selectOption(Button btn, int option_num, int questID){
            if(prevSelectedBtn == null){
                btn.setBackgroundResource(R.drawable.selected_btn);
                DBQuery.g_questList.get(questID).setSelectedAns(option_num);
                changeStatus(questID,DBQuery.ANSWERED);
                prevSelectedBtn = btn;
            }else{
                if(prevSelectedBtn.getId() == btn.getId()){
                    btn.setBackgroundResource(R.drawable.unselected_btn);
                    DBQuery.g_questList.get(questID).setSelectedAns(-1);
                    changeStatus(questID,DBQuery.UNANSWERED);
                    prevSelectedBtn = null;
                }else{
                    prevSelectedBtn.setBackgroundResource(R.drawable.unselected_btn);
                    btn.setBackgroundResource(R.drawable.selected_btn);
                    DBQuery.g_questList.get(questID).setSelectedAns(option_num);
                    changeStatus(questID,DBQuery.ANSWERED);
                    prevSelectedBtn = btn;
                }
            }
        }

        private void changeStatus(int id, int status){
            if(DBQuery.g_questList.get(id).getStatus() != DBQuery.REVIEW){
                DBQuery.g_questList.get(id).setStatus(status);
            }
        }

        private void setOption(Button btn, int option_num, int questID){
            if(DBQuery.g_questList.get(questID).getSelectedAns() == option_num){
                btn.setBackgroundResource(R.drawable.selected_btn);
            }else{
                btn.setBackgroundResource(R.drawable.unselected_btn);
            }
        }
    }
}
