package com.tuapp.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDiagnosticRequest {

    @NotNull(message = "isNormal es obligatorio")
    private Boolean isNormal;

    @NotNull(message = "age es obligatorio")
    @Min(value = 0, message = "La edad no puede ser menor que 0")
    @Max(value = 120, message = "La edad no puede ser mayor que 120")
    private Integer age;

    @NotBlank(message = "gender es obligatorio")
    @Size(max = 20, message = "gender no puede superar 20 caracteres")
    private String gender;

    @Size(max = 255, message = "underlyingDiseases no puede superar 255 caracteres")
    private String underlyingDiseases;

    public Boolean getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(Boolean isNormal) {
        this.isNormal = isNormal;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUnderlyingDiseases() {
        return underlyingDiseases;
    }

    public void setUnderlyingDiseases(String underlyingDiseases) {
        this.underlyingDiseases = underlyingDiseases;
    }
}