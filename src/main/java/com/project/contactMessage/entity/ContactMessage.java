package com.project.contactMessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity

@Data//içinde getter-setter var
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true) //parametreli constructor'ları arka tarafta bizim için setliyor
public class ContactMessage { //public class ContactMessage implements Serializable { apiden dışarı çıkacak
     // setlenen nesnelerin serileştirilmesi gerekir, değişken değerlerinin bir bütün olarak gönderilmesini sağlar
     // restfull api json bizim için bu işlemi yaptığı için yazmıyoruz.
    //Serializable-->nesneyi serileştirir değişken ve değerlerin bir bütün halde gidebilmesini sağlar.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "US")
    private LocalDateTime dateTime;
}