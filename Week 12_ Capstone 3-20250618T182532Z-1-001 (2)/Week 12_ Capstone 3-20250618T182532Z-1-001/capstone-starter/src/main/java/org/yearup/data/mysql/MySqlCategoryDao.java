package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM capstone.categories;";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            )
        {
            while (rs.next()){
                Category category = mapRow(rs);
                categories.add(category);
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Can't retrieve categories.", e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setInt(1,categoryId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                return mapRow(rs);
            }
            else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch (SQLException g){
            g.printStackTrace();
            throw new RuntimeException(g);
        }
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories(name, description) " +
                "VALUES (?, ?)";
        try(Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();


            if (rowsAffected > 0){
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()){
                    int category_id = generatedKeys.getInt(1);
                    return getById(category_id);
                }
            }
        }
        catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add table.");
        }
        return null;
    }


    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String sql = "UPDATE capstone.categories " +
                        "SET name = ? ," +
                        "category_id = ? ," +
                        "description = ? " +
                        "WHERE category_id = ?;";

        try(Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category.getName());
            stmt.setInt(2,category.getCategoryId());
            stmt.setString(3, category.getDescription());
            stmt.setInt(4,categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sql = "DELETE FROM categories " +
                        "WHERE category_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1,categoryId);

            pstmt.executeUpdate();
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            throw new RuntimeException(s);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
