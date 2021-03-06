package main.core.ui.components;

import java.text.SimpleDateFormat;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import main.app.Loader;
import main.core.ui.MainController;
import main.core.ui.MenuController;
import main.core.ui.popups.SubmissionPopupController;
import main.db.LoginManager;
import main.models.Assignment;
import main.models.Submission;
import main.utils.AssignmentStatus;
import main.utils.PostInitialize;
import main.utils.Refreshable;
import main.utils.Status;
import main.utils.View;

public class AssignmentBoxController implements Refreshable {
	@FXML private StackPane rootPane;
	@FXML private Rectangle headerRectangle;
	@FXML private StackPane headerPane;
	@FXML private HBox contentHBox;
    @FXML private Label headerLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label statusLabel;
    @FXML private JFXButton actionButton;

    private SubmissionPopupController submissionController;
    private MainController mainController;
    private Assignment assignment;
    private Submission submission;
    private MenuController menuController;
    
	@FXML
	private void initialize() {
		// Dynamic header rectangle resizing
		headerRectangle.setManaged(false);
		headerRectangle.widthProperty().bind(headerPane.widthProperty());
		headerRectangle.heightProperty().bind(headerPane.heightProperty());
    	submissionController = Loader.getController(View.POPUP_SUBMISSION_VIEW);
    	mainController = Loader.getController(View.MAIN_VIEW);
    	menuController = Loader.getController(View.MENU_VIEW);
	}
	
	public void loadAssignment(Assignment assignment) {
		this.assignment = assignment;
		headerLabel.setText(assignment.getTitle());
		descriptionLabel.setText(assignment.getDescription());
		String formattedDeadline = new SimpleDateFormat("dd. MMM HH:mm").format(assignment.getDeadLine());
		deadlineLabel.setText(formattedDeadline);
	}
	
	public void loadSubmission(Submission submission) {
		this.submission = submission;
	}
	
	/**
	 * Sets styling of AssignmentBox according to student's submission's status.
	 * @param status
	 */
	public void loadSubmissionStatus(Assignment assignment, Submission submission) {
		Status status = Submission.determineStatus(assignment, submission);
		switch (status) {
		case PASSED:
			headerRectangle.getStyleClass().setAll("header-background-green");
			statusLabel.getStyleClass().setAll("status-label-green");
		    statusLabel.setText("Godkjent");
			break;
		case WAITING:
			headerRectangle.getStyleClass().setAll("header-background-orange");
			statusLabel.getStyleClass().setAll("status-label-orange");
			statusLabel.setText("Til vurdering");
			break;
		case FAILED:
			headerRectangle.getStyleClass().setAll("header-background-red");
			statusLabel.getStyleClass().setAll("status-label-red");
			statusLabel.setText("Ikke godkjent");
			break;
		case NOT_DELIVERED:
			headerRectangle.getStyleClass().setAll("header-background-default");
			statusLabel.getStyleClass().setAll("status-label-default");
			statusLabel.setText("Ikke levert");
			break;
		case DEADLINE_EXCEEDED:
			headerRectangle.getStyleClass().setAll("header-background-red");
			statusLabel.getStyleClass().setAll("status-label-red");
			statusLabel.setText("For seint");
			break;
		default:
			break;
		}
	}
	
	/**
	 * Sets styling of AssignmentBox according to assignment's general status.
	 * Only uses a few of given statuses, and combines the rest.
	 * @param status
	 */
	public void loadAssignmentStatus(Assignment assignment) {
		AssignmentStatus assignmentStatus = Assignment.determineStatus(assignment);
		statusLabel.setText(assignmentStatus.getNorwegianName());
		switch (assignmentStatus) {
		case WITHIN_DEADLINE:
			headerRectangle.getStyleClass().setAll("header-background-green");
			statusLabel.getStyleClass().setAll("status-label-green");
			break;
		case DEADLINE_EXCEEDED:
			headerRectangle.getStyleClass().setAll("header-background-red");
			statusLabel.getStyleClass().setAll("status-label-red");
			break;
		default:
			break;
		}
	}
	
	/**
     * Runs any methods that require every controller to be initialized.
     * This method should only be invoked by the FXML Loader class.
     */
    @PostInitialize
    private void postInitialize() {
    }
	
	
	@Override
	public void update() {
	}
	
	@Override
	public void clear() {
	}
	
	@FXML
	void handleOpenAssignmentClick(ActionEvent event) {
		JFXDialog dialog = new JFXDialog(mainController.getOuterStackPane(), (Region) Loader.getParent(View.POPUP_SUBMISSION_VIEW), DialogTransition.CENTER);
		Loader.getParent(View.MENU_VIEW).addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
			dialog.close();
		});
		submissionController.clear();
		submissionController.connectDialog(dialog);
		submissionController.load(assignment, submission, menuController.getSelectedRole());
		dialog.show();		
	}
	
}
