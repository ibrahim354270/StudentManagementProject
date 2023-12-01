package com.project.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)//sadece null olmayan verilerle gitsin, setlediğim değerler gitsin
public class ResponseMessage<E> {//genel bir data türü olsun diye türünü bilmiyoruz <E>
    //responseEntity bağlı olmasın kendi responseEntity mizi yazdık

    private E object;
    private String message;
    private HttpStatus status;
}