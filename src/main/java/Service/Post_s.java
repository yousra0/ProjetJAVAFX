package Service;

import Entity.Comment;
import Entity.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Post_s implements Services <Post>
{
    Connection cnx;

    public Post_s()
    {}
    public Post_s(Connection cnx)
    {
        this.cnx = cnx;
    }
    @Override
    public void add(Post p) throws SQLException
    {
        String qry="INSERT INTO `post`(`titre`, `contenu_pub`, `date_pub`, `file`, `likes`, `dislikes`) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1,p.getTitre());
            pstm.setString(2, p.getContenu_pub());
            pstm.setString(3,p.getDate_pub());
            pstm.setString(4, p.getFile());
            pstm.setInt(5, p.getLikes());
            pstm.setInt(6, p.getDislikes());
            pstm.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public List<Post> show() throws SQLException {
        List<Post> postList = new ArrayList<>();
        String qry = "SELECT * FROM post";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitre(rs.getString("titre"));
                p.setContenu_pub(rs.getString("contenu_pub"));
                p.setDate_pub(rs.getString("date_pub"));
                p.setFile(rs.getString("file"));
                p.setLikes(rs.getInt("likes"));
                p.setDislikes(rs.getInt("dislikes"));

                // Récupérer les commentaires pour le post courant
                List<Comment> comments = getCommentsForPost(p.getId());
                p.setComments(comments);

                postList.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e; // Rethrow the exception to handle it in the caller
        }
        return postList;
    }

    private List<Comment> getCommentsForPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String qry = "SELECT * FROM comment WHERE post_id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, postId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getInt("id"));
                    c.setContenu_comment(rs.getString("contenu_comment"));
                    c.setPost_id(rs.getInt("post_id"));
                    comments.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e; // Rethrow the exception to handle it in the caller
        }
        return comments;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM post WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
    @Override
    public void edit(Post p) throws SQLException
    {
        String sql = "UPDATE `post` SET titre = ?, contenu_pub = ?, date_pub = ?, file = ?, likes = ?, dislikes = ? WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(sql))
        {
            pstm.setString(1, p.getTitre());
            pstm.setString(2, p.getContenu_pub());
            pstm.setString(3, p.getDate_pub());
            pstm.setString(4, p.getFile());
            pstm.setInt(5, p.getLikes());
            pstm.setInt(6, p.getDislikes());
            pstm.setInt(7, p.getId());  // Set the ID parameter
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while editing post: " + e.getMessage());
            throw e;
        }
    }
    public Post getById(int id) throws SQLException
    {
        String qry = "SELECT * FROM post WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry))
        {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery())
            {
                if (rs.next()) {
                    Post p = new Post();
                    p.setId(rs.getInt("id"));
                    p.setTitre(rs.getString("titre"));
                    p.setContenu_pub(rs.getString("contenu_pub"));
                    p.setDate_pub(rs.getString("date_pub"));
                    p.setFile(rs.getString("file"));
                    p.setLikes(rs.getInt("likes"));
                    p.setDislikes(rs.getInt("dislikes"));
                    return p;
                } else {
                    return null; // No tournois found with the given ID
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    public void update(int postId, Post newPost) throws SQLException
    {
        String query = "UPDATE post SET date = ?, titre = ?, Contenu = ?, fichier = ?, nb_likes = ?, nb_dislikes = ? WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, newPost.getDate_pub());
            statement.setString(2, newPost.getTitre());
            statement.setString(3, newPost.getContenu_pub());
            statement.setString(4, newPost.getFile());
            statement.setInt(5, newPost.getLikes());
            statement.setInt(6, newPost.getDislikes());
            statement.setInt(7, postId);

            statement.executeUpdate();
        }
    }
    public void addComment(Post post, Comment comment) throws SQLException {
        String query = "INSERT INTO comment (contenu_comment, post_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(query)) {
            preparedStatement.setString(1, comment.getContenu_comment());
            preparedStatement.setInt(2, post.getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insertion du commentaire a échoué, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
            throw e; // Rethrow the exception to handle it in the caller
        }
    }

}