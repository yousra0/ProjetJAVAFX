package Entity;

import java.util.ArrayList;
import java.util.List;

public class Post
{
    private int id;
    private String titre;
    private String contenu_pub;
    private String date_pub;
    private String file;
    private int likes;
    private int dislikes;

    public List<Comment> getComments()
    {
        return comments;
    }
    private List<Comment> comments = new ArrayList<>();
    public void setComments(List<Comment> comments)
    {
        this.comments = comments;
    }

    public Post(){};
    public Post(String titre, String contenu_pub, String date_pub, String file, int likes, int dislikes) {
        this.titre = titre;
        this.contenu_pub = contenu_pub;
        this.date_pub = date_pub;
        this.file = file;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = new ArrayList<>();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu_pub() {
        return contenu_pub;
    }

    public void setContenu_pub(String contenu_pub) {
        this.contenu_pub = contenu_pub;
    }

    public String getDate_pub() {
        return date_pub;
    }

    public void setDate_pub(String date_pub) {
        this.date_pub = date_pub;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes)
    {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu_pub='" + contenu_pub + '\'' +
                ", date_pub='" + date_pub + '\'' +
                ", file='" + file + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}