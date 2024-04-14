package tn.esprit;

import Entity.Comment;
import Outil.DataBase;
import Entity.Post;
import Service.Comment_s;
import Service.Post_s;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataBase d = DataBase.getInstance();
        Post_s postService = null; // Déclaration à l'extérieur du bloc try
        Comment_s commentService = null;

        try {
            Connection cnx = d.getConn(); // Obtenir la connexion à la base de données
            postService = new Post_s(cnx);
            commentService = new Comment_s(cnx);

            // Add a new comment
            Comment newComment = new Comment(1,1, "New comment text");
            commentService.add(newComment);

            // Retrieve all comments
            List<Comment> comments = commentService.show();
            for (Comment comment : comments) {
                System.out.println(comment);
            }

            // Edit a comment
            Comment commentToUpdate = comments.get(0);
            commentToUpdate.setContenu_comment("Updated comment text");
            commentService.edit(commentToUpdate);

            // Delete a comment
            int commentIdToDelete = comments.get(1).getId();
            commentService.delete(commentIdToDelete);

            // Créer un post avec des valeurs fictives
            Post post = new Post();
            post.setTitre("titre");
            post.setContenu_pub("Contenu");
            post.setDate_pub("2024-03-29");
            post.setFile("Chemin/vers/fichier");
            post.setLikes(1);
            post.setDislikes(0);

            // Ajouter le post à la base de données
            postService.add(post);

            System.out.println("Post ajouté avec succès !");

            // Afficher les posts existants dans la base de données
            List<Post> posts = postService.show();
            System.out.println("Liste des posts :");
            for (Post p : posts) {
                System.out.println("ID : " + p.getId());
                System.out.println("Titre : " + p.getTitre());
                System.out.println("Contenu : " + p.getContenu_pub());
                System.out.println("Date de publication : " + p.getDate_pub());
                System.out.println("Fichier : " + p.getFile());
                System.out.println("Likes : " + p.getLikes());
                System.out.println("Dislikes : " + p.getDislikes());
                System.out.println("--------------------------------");
            }

            // Supprimer le premier post de la liste
            int firstPostId = posts.get(0).getId();
            postService.delete(firstPostId);
            System.out.println("Premier post supprimé avec succès !");

            // Modifier le deuxième post de la liste
            Post secondPost = posts.get(1);
            secondPost.setTitre("Nouveau titre du deuxième post");
            postService.edit(secondPost);
            System.out.println("Deuxième post modifié avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
