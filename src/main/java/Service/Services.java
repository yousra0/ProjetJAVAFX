package Service;

import java.sql.SQLException;
import java.util.List;

public interface Services <T>{
    public void add(T t) throws SQLException;
    public List<T> show() throws SQLException;
    public void delete(int id) throws SQLException;
    public void edit(T t) throws SQLException;




}
