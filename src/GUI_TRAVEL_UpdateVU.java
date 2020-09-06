import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

//combine fight with travel?

public class GUI_TRAVEL_UpdateVU extends Application {
    int fightCount = 0;
    int counter = 2;
    int introCount = 0;
    int safety = 0;
    int mainCount = 0;
    int turn = 0;
    int vsCount = 0;
    int travelCount = 0;
    boolean traveling = false;

    String playerName = "Player";
    Character mainChar;
    Narrator narrate = new Narrator();
    BorderPane mainPane = new BorderPane();
    TextField write = new TextField();
    ProgressBar progressBar;
    Text health;
    Animation animation;
    Scene mainScene;

    Character mouse = new Character("Mouse", 5, 20);
    Character wolf = new Character("Wolf", 10, 40);
    Character bigHonkers = new Character("Big Honkers", 25, 100);
    Character ent = new Character("Ent", 20, 3);
    Character enemy;

    ArrayList<Character> characters = new ArrayList<>(Arrays.asList(mouse, wolf, bigHonkers));
    ArrayList<Character> newChar = new ArrayList<>(Arrays.asList(ent));
    Location mainLocation = new Location("Main", new Coord(0, 0), characters);
    Location newLoc = new Location("New", new Coord(20, 10), newChar);
    Location nextLoc;
    ArrayList<Location> places = new ArrayList<>(Arrays.asList(mainLocation, newLoc));

    public void start(Stage primaryStage) {
        mainLocation.canGo(newLoc);
        mainLocation.getEuclidDistance(newLoc);
        mainScene = new Scene(mainPane, 750, 900);

        Text press = new Text("PRESS ENTER TO START");
        press.setLayoutX(120);
        press.setLayoutY(400);
        mainPane.getChildren().add(press);
        press.setStyle("-fx-fill: white;" + "-fx-font-size: 40px;" + "-fx-font-family: Verdana;");

        mainPane.setPadding(new Insets(20, 20, 20, 20));
        mainPane.setStyle("-fx-background-color: #2d2d2d;");
        mainPane.getStylesheets().add("idk.css");

        mainPane.setBottom(write);
        write.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 28));
        write.setStyle("-fx-background-color: #494949;" + "-fx-text-fill: white;");
        write.setOnKeyPressed(e -> {
            String in = write.getText();
            if (e.getCode() == KeyCode.UP) {
                System.out.println(mainPane.getChildren().get(1));
                if(mainPane.getChildren().get(1).getLayoutY()-mainPane.getChildren().get(5).getLayoutY()<60){
                mainPane.getChildren().forEach(a -> {
                    if (a.getClass() == Text.class && a.getId() == null) {
                        a.setLayoutY(a.getLayoutY() + 85);
                        check();
                    }
                });
            }

            } else if (safety > 0 && animation.getStatus() == Status.RUNNING) {
                animation.jumpTo(animation.getTotalDuration());

            } else if (e.getCode() == KeyCode.DOWN) {
                if(mainPane.getChildren().get(counter-1).getLayoutY()-mainPane.getChildren().get(5).getLayoutY()>300){
                mainPane.getChildren().forEach(a -> {
                    if (a.getClass() == Text.class && a.getId() == null) {
                        a.setLayoutY(a.getLayoutY() - 85);
                        check();
                    }
                });
            }

            } else if (e.getCode() == KeyCode.ENTER && introCount >= 4) {
                if (mainPane.getChildren().get(counter - 1).getLayoutY() > 700) {
                    down();
                }
                if (((int) narrate.think(in).getValue() == 2 && mainChar.getLocation().getDistance(narrate.getNewLoc()) > 0) || traveling) {
                    if (travelCount == 0) {
                        //not universal
                        nextLoc = newLoc;
                        write.clear();
                        Text travel = new Text("");
                        typeWrite("Narrator: " + narrate.think(in).getKey(), travel, 0, 40);
                        mainPane.getChildren().add(travel);
                        counter++;
                        travelCount++;
                        traveling = true;
                    } else if (isFight() || fightCount == 1) {
                        if (vsCount == 0) {
                            write.clear();
                            for (int i = 0; i < places.size(); i++) {
                                if (places.get(i).equals(mainChar.getLocation())) {
                                    enemy = places.get(i).getLives()
                                            .get((int) (Math.random() * places.get(i).getLives().size()));
                                }
                            }
                            Text yvm = new Text("");
                            typeWrite("Narrator: You vs " + enemy.getName(), yvm, 0, 40);
                            mainPane.getChildren().add(yvm);
                            counter++;
                            vsCount++;
                        } else {
                            fight(mainChar, enemy);
                        }
                    } else {
                        Text travelDist = new Text("");
                        typeWrite("Narrator: You have " +  mainChar.getLocation().getDistance(nextLoc) + " steps left.", travelDist,
                                0, 40);
                        mainPane.getChildren().add(travelDist);
                        counter++;
                        mainChar.getLocation().reducDistance(1);
                        if (mainChar.getLocation().getDistance(nextLoc) == 0) {
                            traveling = false;
                            mainChar.setLocation(nextLoc);
                            Text arrived = new Text("");
                            typeWrite("You have arrived at " + nextLoc.getName(), arrived, 0, 40);
                        counter++;
                        mainPane.getChildren().add(arrived);                        }
                    }

                } else {
                    mainPane.getChildren().add((new Text(playerName + ":  " + write.getText())));
                    setLayout(mainPane.getChildren().get(counter), 0, 40);
                    mainPane.getChildren().get(counter)
                            .setStyle("-fx-fill: white;" + "-fx-font-size: 20px;" + "-fx-font-family: Verdana;");
                    counter++;

                    Text text = new Text("");
                    String content = "Narrator: " + narrate.think(in).getKey();
                    typeWrite(content, text, 0, 0);
                    text.setLayoutX(mainPane.getChildren().get(counter - 1).getLayoutX());
                    text.setLayoutY(mainPane.getChildren().get(counter - 1).getLayoutY() + 40);
                    write.clear();
                    counter++;
                    mainPane.getChildren().add(text);
                    check();

                    if (mainPane.getChildren().get(counter - 1).getLayoutY() > 700) {
                        down();
                    }
                }

            } else if (introCount < 4 && e.getCode() == KeyCode.ENTER) {
                if (introCount == 0) {
                    mainPane.getChildren().remove(0);
                    counter--;
                    String introText = "Narrator: Hello, and welcome to the land of iphigenaia, this will be a ---------, \n"
                            + "What is your name? (Please pick something wild)";
                    Text intro = new Text("");
                    typeWrite(introText, intro, 5, -740);
                    mainPane.getChildren().add(intro);
                    counter++;
                    introCount++;

                } else if (introCount == 1) {
                    Text name = new Text("");
                    typeWrite("Narrator: So your name is " + in + "? What a loser.", name, 0, 60);

                    mainChar = new Character(in, 10, 0, mainLocation, 100);
                    System.out.println(mainChar.getLocation());
                    playerName = mainChar.getName();

                    mainPane.getChildren().add(name);
                    write.clear();
                    counter++;

                    health = new Text(" Health: ");
                    health.setId("health");
                    health.setStyle("-fx-fill: white;" + "-fx-font-size: 14px;" + "-fx-font-family: Verdana;");
                    setLayout(health, 0, -110);
                    mainPane.getChildren().add(health);
                    counter++;

                    progressBar = new ProgressBar(1);
                    progressBar.setPrefWidth(200);
                    mainPane.setLeft(progressBar);
                    progressBar.setTranslateX(64);
                    progressBar.setTranslateY(-2);
                    counter++;

                    Text money = new Text("Money: " + mainChar.getMoney());
                    money.setId("money");
                    money.setStyle("-fx-fill: white;" + "-fx-font-size: 14px;" + "-fx-font-family: Verdana;");
                    money.setLayoutX(mainPane.getChildren().get(counter - 2).getLayoutX() + 370);
                    money.setLayoutY(mainPane.getChildren().get(counter - 2).getLayoutY());
                    mainPane.getChildren().add(money);
                    counter++;
                    introCount++;

                }

                else if (introCount == 2) {
                    Text explain = new Text("");
                    String explainString = "Narrator: You might have realized that a health bar and money\nlabel "
                            + "have appeared. They will help  you keep track of your health and \nmoney "
                            + "before you start your adventure lets simulate a battle";
                    typeWrite(explainString, explain, 0, 0);
                    explain.setLayoutX(mainPane.getChildren().get(counter - 4).getLayoutX());
                    explain.setLayoutY(mainPane.getChildren().get(counter - 4).getLayoutY() + 40);
                    mainPane.getChildren().add(explain);
                    counter++;
                    introCount++;
                }

                else if (introCount == 3) {
                    if (mainCount == 0) {
                        Text yvm = new Text("");
                        typeWrite("Narrator: You vs Mouse", yvm, 0, 85);
                        mainPane.getChildren().add(yvm);
                        counter++;
                        mainCount++;
                    } else if (mainCount == 1) {
                        fight(mainChar, mouse);
                    }
                }
            }
        });

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // moves text down
    public void down() {
        mainPane.getChildren().forEach(a -> {
            if (a.getClass() == Text.class && a.getId() == null) {
                a.setLayoutY(a.getLayoutY() - 85);
                check();
            }
        });
    }

    // save?
    public void save() {
    }

    public void check() {
        mainPane.getChildren().forEach(a -> {
            if (a.getClass() == Text.class) {
                if ((a.getLayoutY() >= write.getLayoutY() - 30 || a.getLayoutY() <= health.getLayoutY() + 55)
                        && a.getId() == null) {
                    a.setVisible(false);
                    ;
                } else {
                    if (a.getId() == null) {
                        a.setVisible(true);
                        ;
                    }
                }
            }
        });
    }

    public void typeWrite(String s, Text t, int x, int y) {
        animation = new Transition() {
            {
                setCycleDuration(Duration.millis(30 * s.length()));
            }

            public void interpolate(double frac) {
                int length = s.length();
                int n = Math.round(length * (float) frac);
                t.setStyle("-fx-fill: white;" + "-fx-font-size: 20px;" + "-fx-font-family: Verdana;");
                t.setText(s.substring(0, n));
            }

        };
        setLayout(t, x, y);
        animation.play();
        safety++;
    }

    public void setLayout(Node n, int x, int y) {
        n.setLayoutX(mainPane.getChildren().get(counter - 1).getLayoutX() + x);
        n.setLayoutY(mainPane.getChildren().get(counter - 1).getLayoutY() + y);
    }

    public void fight(Character attacker, Character attackee) {
        if (turn == 0) {
            double mainAtk = attacker.attack();
            Text mainAttack = new Text("");
            attackee.damageTaken((int) mainAtk);
            String mainAtkString = "Narrator: You've done " + (int) (mainAtk) + " damage";
            typeWrite(mainAtkString, mainAttack, 0, 40);
            mainPane.getChildren().add(mainAttack);
            counter++;
            turn++;
            if (attackee.getHealth() < 1) {
                introCount++;
                fightCount = 0;
                turn = 0;
                vsCount = 0;
                attackee.reset();
            }
        } else if (turn == 1) {
            double enemAtk = attackee.attack();
            Text enemAttack = new Text("");
            attacker.damageTaken((int) enemAtk);
            String enemString = "Narrator: " + attackee.getName() + "(" + (int) attackee.getHealth() + ")"
                    + " has done " + (int) enemAtk + " damage";
            typeWrite(enemString, enemAttack, 0, 40);
            mainPane.getChildren().add(enemAttack);
            progressBar.setProgress((mainChar.getHealth() - enemAtk) / mainChar.getMaxHealth());
            counter++;
            turn = 0;
            if (mainPane.getChildren().get(counter - 1).getLayoutY() > 700) {
                down();
            }
        }
    }

    public boolean isFight() {
        if (Math.random() > 0.95) {
            fightCount = 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}