package com.example.ddd_demo.application.annotation;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)               // クラスに付与する
@Retention(RetentionPolicy.RUNTIME)     // 実行時まで保持する
@Component                              // Springコンポーネントとして登録
public @interface UseCase {
}
