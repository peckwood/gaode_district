package org.example;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Response implements Serializable {
    private String status;
    private String info;
    private String infocode;
    private List<District> districts;
    private Long count;
    private Object suggestion;

}
