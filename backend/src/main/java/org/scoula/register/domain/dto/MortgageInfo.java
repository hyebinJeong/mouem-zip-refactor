package org.scoula.register.domain.dto;

public class MortgageInfo {
    private String rank;
    private String registrationPurpose;
    private String registrationCause;
    private String maxClaimAmount;
    private String mortgageHolder;

    // 생성자, getter, setter

    public MortgageInfo() {}

    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRegistrationPurpose() {
        return registrationPurpose;
    }
    public void setRegistrationPurpose(String registrationPurpose) {
        this.registrationPurpose = registrationPurpose;
    }

    public String getRegistrationCause() {
        return registrationCause;
    }
    public void setRegistrationCause(String registrationCause) {
        this.registrationCause = registrationCause;
    }

    public String getMaxClaimAmount() {
        return maxClaimAmount;
    }
    public void setMaxClaimAmount(String maxClaimAmount) {
        this.maxClaimAmount = maxClaimAmount;
    }

    public String getMortgageHolder() {
        return mortgageHolder;
    }
    public void setMortgageHolder(String mortgageHolder) {
        this.mortgageHolder = mortgageHolder;
    }
}
