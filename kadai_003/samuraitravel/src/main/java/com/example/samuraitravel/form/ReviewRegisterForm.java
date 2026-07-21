package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRegisterForm {
        
    @NotBlank(message = "コメントを入力してください。")
    private String content;   
        
    @NotNull(message = "評価を選択してください。")
    @Max(value = 5)
    private Integer score = 5;    
    
}