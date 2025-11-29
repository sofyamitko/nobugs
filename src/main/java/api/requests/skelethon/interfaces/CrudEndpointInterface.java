package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel baseModel);
    Object get(Integer id);
    Object get();
    Object update(BaseModel baseModel);
    Object delete(Integer id);
}
