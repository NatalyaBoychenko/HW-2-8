package org.example.client;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class ClientService implements ClientDao {
    private final Connection con;

    @Override
    public long create(String name) {
        if (name.length() < 2 || name.length() > 1000){
            throw new IllegalArgumentException("Name's length must be greater than 2 and less than 1000");
        }

        try (PreparedStatement statement = con.prepareStatement("INSERT INTO client (name) VALUES ?")){

            statement.setString(1, name);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        long id = 0L;

        try (PreparedStatement st = con.prepareStatement("SELECT max(id) as max_id FROM client");
             ResultSet rs = st.executeQuery()){

            rs.next();
            id = rs.getLong("max_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    @Override
    public String getById(long id) {
        if (id < 1) {
            throw new IllegalArgumentException("ID must be greater than 1");
        }

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = con.prepareStatement("SELECT * FROM client WHERE id = ?");
            st.setLong(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                return rs.getString("name");
            }else {
                throw new NoSuchElementException("There is no client with id " + id);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                rs.close();
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return null;
    }

    @Override
    public void setName(long id, String name) {
        if (id < 1) {
            throw new IllegalArgumentException("ID must be greater than 1");
        }

        if (name.length() < 2 || name.length() > 1000){
            throw new IllegalArgumentException("Name's length must be greater than 2 and less than 1000");
        }

        try (PreparedStatement st = con.prepareStatement("UPDATE client SET name = ? WHERE id = ?")){

            st.setString(1, name);
            st.setLong(2, id);

            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (PreparedStatement st = con.prepareStatement("DELETE FROM client WHERE id = ?")){
            st.setLong(1, id);
            st.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Client> listAll() {
        List<Client> clients = new ArrayList<>();

        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM client");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getLong("id"));
                client.setName(rs.getString("name"));
                clients.add(client);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return clients;
    }
}
