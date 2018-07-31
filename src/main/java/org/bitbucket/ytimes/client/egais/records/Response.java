package org.bitbucket.ytimes.client.egais.records;

public class Response<T> {

    public boolean completed = true;
    public T data;

    public Response() {
    }

    public Response(boolean completed) {
        this.completed = completed;
    }
}
