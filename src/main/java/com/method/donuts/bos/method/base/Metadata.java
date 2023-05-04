package com.method.donuts.bos.method.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Metadata {

    private String dunkinId;

    private String destinationDunkinId;

    private String sourceDunkinId;

    public Metadata(String dunkinId) {
        this.dunkinId = dunkinId;
    }

    public Metadata(String sourceDunkinId, String destinationDunkinId) {
        this.destinationDunkinId = destinationDunkinId;
        this.sourceDunkinId = sourceDunkinId;
    }

    public Metadata() {

    }
}
