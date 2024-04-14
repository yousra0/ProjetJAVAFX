package Controller;

import Entity.Comment;
import Entity.Post;
import Outil.DataBase;
import Service.Comment_s;
import Service.Post_s;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.itextpdf.text.pdf.PdfPTable;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.Image;
//import com.itextpdf.text.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddPost
{
    @FXML
    private TableView<Post> postTab;
    @FXML
    private MFXTextField titreTextField;
    @FXML
    private MFXTextField contenuTextField;
    @FXML
    private MFXDatePicker dateTextField;
    @FXML
    private MFXTextField fichierTextField;
    @FXML
    private MFXTextField likesTextField;
    @FXML
    private MFXTextField dislikesTextField;
    @FXML
    private ImageView imageviewFile;
    @FXML
    private MFXTextField searchTextField;
    @FXML
    private MFXTextField commentTextField;
    @FXML
    private MFXButton editbtn;
    @FXML
    private Button openMapButton;

    @FXML
    private TableColumn<Post, String> titretf;
    @FXML
    private TableColumn<Post, String> contenutf;
    @FXML
    private TableColumn<Post, String> datetf;
    @FXML
    private TableColumn<Post, String> fichiertf;
    @FXML
    private TableColumn<Post, Integer> likestf;
    @FXML
    private TableColumn<Post, Integer> dislikestf;
    @FXML
    private TableColumn<Post, String> commentstf;
    @FXML
    private TableColumn<Post, Image> imagetf;
    @FXML
    private TableColumn<Post, Integer> idcl;
    public TableView<Post> getPostTab()
    {
        return postTab;
    }
    Connection conn = DataBase.getInstance().getConn();
    Post_s postS = new Post_s(conn);

    @FXML
    public ObservableList<Post> getPost()
    {
        ObservableList<Post> post = FXCollections.observableArrayList();
        String query = "SELECT * FROM post";
        Statement st;
        ResultSet rs;
        try
        {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            while (rs.next())
            {
                Post r = new Post();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("Titre")); // Correction du nom de la colonne
                r.setContenu_pub(rs.getString("contenu_pub"));
                r.setDate_pub(rs.getString("date_pub"));
                r.setFile(rs.getString("file"));
                r.setLikes(rs.getInt("likes"));
                r.setDislikes(rs.getInt("dislikes"));
                post.add(r);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return post;
    }
    @FXML
    void deletePost(Post post)
    {
        try
        {
            postS.delete(post.getId());
            postTab.getItems().remove(post);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Deletion Failed");
            errorAlert.setContentText("An error occurred while deleting the post. Please try again.");
            errorAlert.showAndWait();
        }
    }
    public void show() throws SQLException
    {
        ObservableList<Post> list = getPost();
        idcl.setCellValueFactory(new PropertyValueFactory<>("id"));
        titretf.setCellValueFactory(new PropertyValueFactory<>("titre"));
        contenutf.setCellValueFactory(new PropertyValueFactory<>("contenu_pub"));
        datetf.setCellValueFactory(new PropertyValueFactory<>("date_pub"));
        fichiertf.setCellValueFactory(new PropertyValueFactory<>("file"));
        likestf.setCellValueFactory(new PropertyValueFactory<>("likes"));
        dislikestf.setCellValueFactory(new PropertyValueFactory<>("dislikes"));
        commentstf.setCellValueFactory(new PropertyValueFactory<>("comments")); // Assurez-vous que la classe Post a une propriété comments avec ses getters et setters appropriés
        // Ajouter les commentaires pour le post
        TableColumn<Post, Void> col_comments = new TableColumn<>("Comments");
        col_comments.setCellFactory(param -> new TableCell<>()
        {
            private final Button commentsButton = new Button("View Comments");
            {
                commentsButton.setOnAction(event -> {
                    Post post = getTableView().getItems().get(getIndex());
                    showComments(post);
                });
            }
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    Post post = getTableView().getItems().get(getIndex());
                    deletePost(post);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        postTab.getColumns().add(col_comments);
        postTab.setItems(list);
    }
    @FXML
    void add(ActionEvent event) throws SQLException
    {
        // Vérifier la longueur des champs titreTextField et contenuTextField
        if (titreTextField.getText().length() < 2 || titreTextField.getText().length() > 10 ||
                contenuTextField.getText().length() < 2 || contenuTextField.getText().length() > 10)
        {
            afficherErreur("La taille du titre et du contenu doit être entre 2 et 10 caractères.");
            return;
        }
        // Vérifier si les champs obligatoires sont remplis
        if (titreTextField.getText().isEmpty() || contenuTextField.getText().isEmpty() || dateTextField.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.show();
            return;
        }
        // Vérifier si les champs obligatoires sont remplis
        if (titreTextField.getText().isEmpty() || contenuTextField.getText().isEmpty() || dateTextField.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.show();
            return;
        }
        // Vérifier si les champs likes et dislikes contiennent des nombres valides
        int likes, dislikes;
        try
        {
            likes = Integer.parseInt(likesTextField.getText());
            dislikes = Integer.parseInt(dislikesTextField.getText());
        }
        catch (NumberFormatException e)
        {
            afficherErreur("Les champs likes et dislikes doivent être des nombres entiers.");
            return;
        }
        // Vérifier si les nombres de likes et dislikes sont positifs
        if (likes < 0 || dislikes < 0)
        {
            afficherErreur("Les nombres de likes et dislikes doivent être positifs.");
            return;
        }
        // Récupérer le chemin du fichier image
        String file = fichierTextField.getText();
        // Vérifier si le fichier existe
        if (!Files.exists(Paths.get(file)))
        {
            afficherErreur("Le fichier spécifié n'existe pas.");
            return;
        }
        LocalDate selectedDate = dateTextField.getValue();
        // Format the selected date into a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = selectedDate.format(formatter);
        Post post = new Post(titreTextField.getText(), contenuTextField.getText(), dateString, fichierTextField.getText(), Integer.parseInt(likesTextField.getText()), Integer.parseInt(dislikesTextField.getText()));
        try
        {
            // Ajouter les commentaires pour le post
            List<Comment> comments = new ArrayList<>();
            post.setComments(comments);
            postS.add(post);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("post ajouté avec succés");
            alert.show();
            //show
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPost.fxml"));
            postTab.getItems().clear();
            postTab.getItems().addAll(getPost());
        }
        catch (NumberFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Veuillez saisir un nombre valide pour le nombre de likes et de dislikes.");
            alert.show();
        }
    }
    @FXML
    void edit(ActionEvent event)
    {
        Post selectedPost = postTab.getSelectionModel().getSelectedItem();
        if (selectedPost != null)
        {
            try
            {
                // Récupérer les nouvelles valeurs des champs
                String newTitre = titreTextField.getText();
                String newContenu = contenuTextField.getText();
                LocalDate newDate = dateTextField.getValue(); // Récupérer la valeur LocalDate
                // Vérifier si la nouvelle date n'est pas null
                String newDateString = (newDate != null) ? newDate.toString() : "";
                String newFile = fichierTextField.getText();
                int newLikes = Integer.parseInt(likesTextField.getText());
                int newDislikes = Integer.parseInt(dislikesTextField.getText());
                // Mettre à jour les valeurs du post sélectionné
                selectedPost.setTitre(newTitre);
                selectedPost.setContenu_pub(newContenu);
                selectedPost.setDate_pub(newDateString);
                selectedPost.setFile(newFile);
                selectedPost.setLikes(newLikes);
                selectedPost.setDislikes(newDislikes);
                // Mettre à jour les commentaires pour le post
                List<Comment> comments = new ArrayList<>();
                selectedPost.setComments(comments);
                // Mettre à jour le post dans la base de données
                postS.edit(selectedPost);
                // Rafraîchir la TableView pour afficher les modifications
                postTab.getItems().clear();
                postTab.getItems().addAll(getPost());
                // Afficher un message de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Post modifié avec succès.");
                alert.show();
            } catch (NumberFormatException e)
            {
                // En cas d'erreur de formatage des nombres
                afficherErreur("Veuillez saisir un nombre valide pour les likes et les dislikes.");
            } catch (SQLException e)
            {
                // En cas d'erreur lors de la mise à jour de la base de données
                afficherErreur("Une erreur s'est produite lors de la mise à jour du post : " + e.getMessage());
            }
        } else
        {
            // Si aucun post n'est sélectionné
            afficherErreur("Veuillez sélectionner un post à modifier.");
        }
    }
    private String getCommentsAsString(Post post)
    {
        StringBuilder builder = new StringBuilder();
        List<Comment> comments = post.getComments();
        for (Comment comment : comments) {
            builder.append(comment.getContenu_comment()).append("\n");
        }
        return builder.toString();
    }

    @FXML
    void initialize() throws SQLException
    {
        idcl.setCellValueFactory(new PropertyValueFactory<>("id"));
        titretf.setCellValueFactory(new PropertyValueFactory<>("titre"));
        contenutf.setCellValueFactory(new PropertyValueFactory<>("contenu_pub"));
        datetf.setCellValueFactory(new PropertyValueFactory<>("date_pub"));
        fichiertf.setCellValueFactory(new PropertyValueFactory<>("file"));
        likestf.setCellValueFactory(new PropertyValueFactory<>("likes"));
        dislikestf.setCellValueFactory(new PropertyValueFactory<>("dislikes"));
        // Afficher les commentaires dans la colonne "contenu_comment"
        commentstf.setCellValueFactory(new PropertyValueFactory<>("contenu_comment"));
        commentstf.setCellValueFactory(cellData -> {
            Post post = cellData.getValue();
            return new SimpleStringProperty(getCommentsAsString(post));
        });
        // Définir une cellule personnalisée pour afficher l'image dans la colonne "Fichier"
        fichiertf.setCellFactory(column -> {
            return new TableCell<Post, String>()
            {
                private final ImageView imageView = new ImageView();
                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);
                    if (empty || imagePath == null)
                    {
                        setGraphic(null);
                    } else
                    {
                        // Charger et afficher l'image
                        try
                        {
                            File file = new File(imagePath);
                            Image image = new Image(file.toURI().toString());
                            imageView.setImage(image);
                            imageView.setFitWidth(300); // Définir la largeur souhaitée de l'image
                            imageView.setFitHeight(100); // Définir la hauteur souhaitée de l'image
                            setGraphic(imageView);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            setGraphic(null);
                        }
                    }
                }
            };
        });
        show();
        postTab.setOnMouseClicked(this::handleRowClick);
    }
    private void handleRowClick(MouseEvent event) {
        if (!postTab.getSelectionModel().isEmpty()) {
            Post selectedPost = postTab.getSelectionModel().getSelectedItem();
            titretf.setText(selectedPost.getTitre());
            contenutf.setText(selectedPost.getContenu_pub());
            datetf.setText(selectedPost.getDate_pub());
            fichiertf.setText(selectedPost.getFile());
            likestf.setText(Integer.toString(selectedPost.getLikes()));
            dislikestf.setText(Integer.toString(selectedPost.getDislikes()));
            // Récupérer les commentaires pour le post sélectionné
            StringBuilder commentsText = new StringBuilder();
            for (Comment comment : selectedPost.getComments()) {
                commentsText.append(comment.getContenu_comment()).append("\n");
            }
            commentstf.setText(commentsText.toString());
        }
    }
    @FXML
    private void choisirImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            String imagePath = file.toURI().toString();
            Image image = new Image(imagePath);
            imageviewFile.setImage(image);
            fichierTextField.setText(file.getAbsolutePath());
        }
    }
    private void afficherErreur(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
    private void showComments(Post post)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Comments for Post: " + post.getTitre());
        StringBuilder commentsText = new StringBuilder();
        for (Comment comment : post.getComments())
        {
            commentsText.append(comment.getContenu_comment()).append("\n");
        }
        alert.setContentText(commentsText.toString());
        alert.showAndWait();
    }
    @FXML
    private void exporterEnPDF(ActionEvent event)
    {
        Document document = new Document();
        try
        {
            // Générer un nom de fichier unique
            String fileName = "export_" + System.currentTimeMillis() + ".pdf";
            // Chemin d'accès où le fichier PDF sera enregistré
            String pathToSave = System.getProperty("user.home") + File.separator + "Documents" + File.separator + fileName;
            PdfWriter.getInstance(document, new FileOutputStream(pathToSave));
            document.open();
            // Ajouter un titre au document
            document.add(new Paragraph("Liste des posts"));
            // Créer un tableau avec 7 colonnes pour les données des posts (ajout de la colonne pour l'image)
            PdfPTable table = new PdfPTable(6);
            // Ajouter les en-têtes du tableau
            table.addCell("Titre");
            table.addCell("contenu_pub");
            table.addCell("date_pub");
            table.addCell("filee");
            table.addCell("Likes");
            table.addCell("Dislikes");
            // Ajouter les données des posts dans le tableau
            for (Post post : postTab.getItems())
            {
                table.addCell(post.getTitre());
                table.addCell(post.getContenu_pub());
                table.addCell(post.getDate_pub());
                table.addCell(post.getFile());
                table.addCell(String.valueOf(post.getLikes()));
                table.addCell(String.valueOf(post.getDislikes()));
            }
            // Ajouter le tableau au document
            document.add(table);
            document.close();
            afficherInformation("Les données ont été exportées en PDF avec succès.");
        } catch (IOException | DocumentException e)
        {
            afficherErreur("Une erreur s'est produite lors de l'exportation en PDF : " + e.getMessage());
        }
    }
    private void afficherInformation(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
    @FXML
    void search(ActionEvent event)
    {
        String searchText = searchTextField.getText().toLowerCase();
        ObservableList<Post> filteredPosts = FXCollections.observableArrayList();
        if (searchText.isEmpty())
        {
            postTab.setItems(getPost());
        } else
        {
            for (Post post : getPost())
            {
                if (post.getTitre().toLowerCase().contains(searchText) ||
                        post.getContenu_pub().toLowerCase().contains(searchText) ||
                        post.getDate_pub().toLowerCase().contains(searchText) ||
                        post.getFile().toLowerCase().contains(searchText) ||
                        String.valueOf(post.getLikes()).contains(searchText) ||
                        String.valueOf(post.getDislikes()).contains(searchText)) {
                    filteredPosts.add(post);
                }
            }
            postTab.setItems(filteredPosts);
        }
    }
    @FXML
    void addComment(ActionEvent event) throws SQLException
    {
        Post selectedPost = postTab.getSelectionModel().getSelectedItem();
        if (selectedPost != null)
        {
            String commentText = commentTextField.getText();
            if (commentText.isEmpty())
            {
                afficherErreur("Veuillez saisir un commentaire.");
                return;
            }
            Comment comment = new Comment(commentText);
            try
            {
                postS.addComment(selectedPost, comment);
                postTab.getItems().clear();
                postTab.getItems().addAll(getPost());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Commentaire ajouté avec succès.");
                alert.show();
                commentTextField.setText("");
            }
            catch (SQLException e)
            {
                afficherErreur("Une erreur s'est produite lors de l'ajout du commentaire : " + e.getMessage());
            }
        }
        else
        {
            afficherErreur("Veuillez sélectionner un post pour ajouter un commentaire.");
        }
    }

    @FXML
    void openMap(ActionEvent event)
    {
        Stage stage = new Stage();
        GluonMapExample mapExample = new GluonMapExample();
        mapExample.start(stage);

        Node node = (Node) event.getSource(); // Récupère le Node déclenchant l'événement
        Scene scene = node.getScene(); // Récupère la scène à partir du Node
        MapView mapView = (MapView) scene.lookup("#mapView"); // Remplacez "mapView" par l'ID de votre MapView
        MapPoint espritLocation = new MapPoint(36.89830009644064, 10.186927429895055);
        CustomCircleMarkerLayer markerLayer = new CustomCircleMarkerLayer(espritLocation);
        mapView.addLayer(markerLayer);
    }
}