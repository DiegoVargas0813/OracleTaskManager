package com.springboot.MyTodoList.util;

public class UserState {
    public enum Process {
        TASK_CREATION, CURRENT_SPRINT_TASK,TASK_COMPLETION,EMAIL_VERIFICATION, OTHER_PROCESS, NONE
    }

    private Process currentProcess;
    private Object processState;

    public UserState() {
        this.currentProcess = Process.EMAIL_VERIFICATION;
        this.processState = null;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

    public Object getProcessState() {
        return processState;
    }

    public void setProcessState(Object processState) {
        this.processState = processState;
    }
}