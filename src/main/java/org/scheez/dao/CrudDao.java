package org.scheez.dao;

import java.util.List;

public interface CrudDao<T, U>
{
    T get (U id);
    
    List<T> getAll();
    
    void insert (T t);
    
    void insert (List<T> t);
    
    void update (T t);
    
    void upsert (T t);
    
    void delete (U id);
    
    void deleteAll ();
}
