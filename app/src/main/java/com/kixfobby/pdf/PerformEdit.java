package com.kixfobby.pdf;

import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Stack;


public class PerformEdit {
    
    int index;
    
    Stack<Action> history = new Stack<>();
    
    Stack<Action> historyBack = new Stack<>();

    private Editable editable;
    private EditText editText;
    
    private boolean flag = false;

    public PerformEdit(@NonNull EditText editText) {
        CheckNull(editText, "EditText");
        this.editable = editText.getText();
        this.editText = editText;
        editText.addTextChangedListener(new Watcher());
    }

    protected void onEditableChanged(Editable s) {

    }

    protected void onTextChanged(Editable s) {

    }


    public final void clearHistory() {
        history.clear();
        historyBack.clear();
    }


    public final void undo() {
        if (history.empty()) return;
        
        flag = true;
        Action action = history.pop();
        historyBack.push(action);
        if (action.isAdd) {
            
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        } else {
            
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        }
        
        flag = false;
        
        if (!history.empty() && history.peek().index == action.index) {
            undo();
        }
    }

    
    public final void redo() {
        if (historyBack.empty()) return;
        flag = true;
        Action action = historyBack.pop();
        history.push(action);
        if (action.isAdd) {
            
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        } else {

            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        }
        flag = false;
        
        if (!historyBack.empty() && historyBack.peek().index == action.index)
            redo();
    }

    
    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        editable.replace(0, editable.length(), text);
        flag = false;
    }

    private class Watcher implements TextWatcher {

        
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, false);
                    if (count > 1) {
                        
                        action.setSelectCount(count);
                    } else if (count == 1 && count == after) {
                        
                        action.setSelectCount(count);
                    }
                    

                    history.push(action);
                    historyBack.clear();
                    action.setIndex(++index);
                }
            }
        }

        
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, true);

                    history.push(action);
                    historyBack.clear();
                    if (before > 0) {
                        
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != editable) {
                editable = s;
                onEditableChanged(s);
            }
            PerformEdit.this.onTextChanged(s);
        }

    }

    private class Action {
        
        CharSequence actionTarget;
        
        int startCursor;
        int endCursor;
        
        boolean isAdd;
        
        int index;


        public Action(CharSequence actionTag, int startCursor, boolean add) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.isAdd = add;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    private static void CheckNull(Object o, String message) {
        if (o == null) throw new IllegalStateException(message);
    }
}
