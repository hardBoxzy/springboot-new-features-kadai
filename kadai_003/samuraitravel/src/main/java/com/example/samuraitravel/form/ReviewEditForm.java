package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
	 @NotNull
	 private Integer id; 
	 
	    @NotNull(message = "評価を選択してください。")
	    @Max(value = 5)
	    private Integer score = 5;   
	    
    @NotBlank(message = "コメントを入力してください。")
    private String content;   
        
 
    
}