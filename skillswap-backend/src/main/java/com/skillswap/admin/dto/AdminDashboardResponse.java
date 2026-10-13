package com.skillswap.admin.dto;

public class AdminDashboardResponse {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    private long totalColleges;
    private long activeColleges;
    private long pendingColleges;

    private long totalCompanies;
    private long activeCompanies;
    private long pendingCompanies;

    private long totalAuditLogs;

    public AdminDashboardResponse() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public long getTotalColleges() {
        return totalColleges;
    }

    public void setTotalColleges(long totalColleges) {
        this.totalColleges = totalColleges;
    }

    public long getActiveColleges() {
        return activeColleges;
    }

    public void setActiveColleges(long activeColleges) {
        this.activeColleges = activeColleges;
    }

    public long getPendingColleges() {
        return pendingColleges;
    }

    public void setPendingColleges(long pendingColleges) {
        this.pendingColleges = pendingColleges;
    }

    public long getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(long totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public long getActiveCompanies() {
        return activeCompanies;
    }

    public void setActiveCompanies(long activeCompanies) {
        this.activeCompanies = activeCompanies;
    }

    public long getPendingCompanies() {
        return pendingCompanies;
    }

    public void setPendingCompanies(long pendingCompanies) {
        this.pendingCompanies = pendingCompanies;
    }

    public long getTotalAuditLogs() {
        return totalAuditLogs;
    }

    public void setTotalAuditLogs(long totalAuditLogs) {
        this.totalAuditLogs = totalAuditLogs;
    }
}