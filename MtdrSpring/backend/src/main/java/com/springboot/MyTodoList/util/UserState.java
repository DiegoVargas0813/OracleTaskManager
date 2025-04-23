package com.springboot.MyTodoList.util;

public class UserState {
    public enum Process {
        TASK_CREATION, 
        CURRENT_SPRINT_TASK,
        TASK_COMPLETION,
        EMAIL_VERIFICATION, 
        OTHER_PROCESS, 
        NONE
    }

    public enum Role {
        USER, 
        MANAGER,
        NONE
    }

    private Process currentProcess;
    private Object processState;
    private Role role;

    public UserState() {
        this.role = Role.NONE;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}