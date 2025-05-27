package matchcardsgame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.sound.sampled.*;
import java.net.URL;


public class MatchCards {
    //attributes
    Clip backgroundClip;
    Clip victoryClip;
    
    String[] cardList = { 
        "clint",
        "lukas",
        "suyou",
        "johnson",
        "joy",
        "kalea",
        "gatotkaca",
        "cecil",
        "angela",
        "harith",
        "julian",
        "Cici",
        "Zhuxin",
        "Chip",
        "Gloo",
        "floryn",
        "badang",
        "carmila",
        "hanzo",
        "vexana",
        "pharsa",
        "novaria",
        "wanwan",
        "mathilda",
        "Atlas"
    };

    ArrayList<Card> cardSet; 
    ImageIcon cardBackImageIcon;
    
    int rows = 4;
    int columns = 5;
    int cardWidth = 140;
    int cardHeight = 170;
    int boardWidth = 700; 
    int boardHeight = 680; 
    
    JFrame frame = new JFrame("Mobile Legends Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();
    JButton card1Selected;
    JButton card2Selected;
    
    ActionListener tileClickListener;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    Timer startLevelTimer;
    boolean gameReady = false;
    
    int level = 1;
    int errorCount = 0;
    int maxErrors = 10;
    
    //inner class card
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }
    
    //constructor
    MatchCards() {
        URL bgMusicURL = getClass().getClassLoader().getResource("res/background.wav");
        playBackgroundMusic(bgMusicURL);
        setupTileClickListener();
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Level "  + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardBackImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(tileClickListener);

            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                setupCards(); 
                shuffleCards(); 


                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }

                errorCount = 0;
                textLabel.setText("Level "  + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon); 
                }

                startLevelTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
        for (int i = 0; i < board.size(); i++) {
           board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }
        hideCardTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);

        startLevelTimer = new Timer(5000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        hideCards(); 
        }
        });
        startLevelTimer.setRepeats(false);
        startLevelTimer.start();
    }
    
    //Game Setup
    void setupCards() {
    cardSet = new ArrayList<>();
    ArrayList<String> cardPool = new ArrayList<>();
    for (String name : cardList) cardPool.add(name);
    java.util.Collections.shuffle(cardPool);

    int pairCount = getPairCountForLevel(level);
    if (pairCount > cardList.length) pairCount = cardList.length;

    ArrayList<String> selectedCards = new ArrayList<>(cardPool.subList(0, pairCount));

    for (String cardName : selectedCards) {
        URL imgURL = getClass().getClassLoader().getResource("res/" + cardName + ".png");
        if (imgURL != null) {
            Image cardImg = new ImageIcon(imgURL).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        } else {
            System.err.println("Image not found for card: " + cardName);
        }
    }

    cardSet.addAll(new ArrayList<>(cardSet)); 

    URL backURL = getClass().getClassLoader().getResource("res/back.jpg");
    if (backURL != null) {
        Image cardBackImg = new ImageIcon(backURL).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    } else {
        System.err.println("Card back image not found!");
    }
}
    
     void shuffleCards() {
        System.out.println(cardSet);
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size()); 
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println(cardSet);
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) { 
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        }
        else { 
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
    
    void resetBoard() {
    boardPanel.removeAll();
    board.clear();

    int totalCards = cardSet.size();
    int gridRows = rows;
    int gridCols = totalCards / gridRows;
    if (totalCards % gridRows != 0) gridCols++;

    boardPanel.setLayout(new GridLayout(gridRows, gridCols));

    for (int i = 0; i < totalCards; i++) {
        JButton newTile = new JButton();
        newTile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        newTile.setOpaque(true);
        newTile.setIcon(cardSet.get(i).cardImageIcon);
        newTile.setFocusable(false);
        newTile.addActionListener(tileClickListener);
        board.add(newTile);
        boardPanel.add(newTile);
    }

    boardPanel.revalidate();
    boardPanel.repaint();
}
    
     
    void showFinalImagePopup() {
    if (backgroundClip != null && backgroundClip.isRunning()) {
        backgroundClip.stop();
    }

    URL victorySoundURL = getClass().getClassLoader().getResource("res/victory.wav");
    playSoundEffect(victorySoundURL);

    URL victoryImgURL = getClass().getClassLoader().getResource("res/victory.png");
    if (victoryImgURL != null) {
        ImageIcon imageIcon = new ImageIcon(victoryImgURL);
        Image scaledImg = imageIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        JLabel imageLabel = new JLabel(scaledIcon);
        JOptionPane.showMessageDialog(frame, imageLabel, "Congratulations!", JOptionPane.PLAIN_MESSAGE);
    } else {
        System.err.println("Victory image not found!");
    }

    URL bgMusicURL = getClass().getClassLoader().getResource("res/background.wav");
    playBackgroundMusic(bgMusicURL);
}
    
    void setupTileClickListener() {
    tileClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameReady) return;

            JButton tile = (JButton) e.getSource();
            if (tile.getIcon() == cardBackImageIcon) {
                if (card1Selected == null) {
                    card1Selected = tile;
                    int index = board.indexOf(card1Selected);
                    card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                   URL flipSoundURL=getClass().getClassLoader().getResource("res/flip.wav");
                   playSoundEffect(flipSoundURL);

                } else if (card2Selected == null) {
                    card2Selected = tile;
                    int index = board.indexOf(card2Selected);
                    card2Selected.setIcon(cardSet.get(index).cardImageIcon);
URL flipSoundURL=getClass().getClassLoader().getResource("res/flip.wav");
                   playSoundEffect(flipSoundURL);
                    if (card1Selected.getIcon() != card2Selected.getIcon()) {
URL incorrectSoundURL=getClass().getClassLoader().getResource("res/incorrect.wav");
playSoundEffect(incorrectSoundURL);
                        errorCount++;
textLabel.setText("Level "  + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);
                        if (errorCount >= maxErrors) {
URL failSoundURL= getClass().getClassLoader().getResource("res/fail.wav");
playSoundEffect(failSoundURL);
        JOptionPane.showMessageDialog(frame, "Too many mistakes! Restarting Level " + getLevelName(level));

    card1Selected = null;
    card2Selected = null;
    errorCount = 0;

    setupCards();
    shuffleCards();

    boardPanel.removeAll();
    board.clear();

    int totalCards = cardSet.size();
    int gridRows = rows; 
    int gridCols = totalCards / gridRows;
    if (totalCards % gridRows != 0) gridCols++;

    boardPanel.setLayout(new GridLayout(gridRows, gridCols));

    for (int i = 0; i < totalCards; i++) {
        JButton newTile = new JButton();
        newTile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        newTile.setOpaque(true);
        newTile.setIcon(cardSet.get(i).cardImageIcon);
        newTile.setFocusable(false);
        newTile.addActionListener(tileClickListener);
        board.add(newTile);
        boardPanel.add(newTile);
    }

    boardPanel.revalidate();
    boardPanel.repaint();

    textLabel.setText("Level " + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);

    gameReady = false;
    restartButton.setEnabled(false);

    startLevelTimer.start();
}
else {
                            hideCardTimer.start();
                        }

                    } else {
playSoundEffect(getClass().getClassLoader().getResource("res/correct.wav"));

                        card1Selected = null;
                        card2Selected = null;

                        boolean allMatched = true;
                        for (JButton btn : board) {
                            if (btn.getIcon() == cardBackImageIcon) {
                                allMatched = false;
                                break;
                            }
                        }
                        if (allMatched) {
                            URL successSoundURL=getClass().getClassLoader().getResource("res/success.wav");
                            playSoundEffect(successSoundURL);
                            
                            String levelName = getLevelName(level);
    JOptionPane.showMessageDialog(frame, "Level " +  levelName + " completed!");
                            level++;
errorCount = 0;

if (level == 5) {
    showFinalImagePopup();
    level = 1; 
    errorCount = 0;
    textLabel.setText("Level " + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);
    setupCards();
    shuffleCards();
    resetBoard(); 
    startLevelTimer.start();
    return;
}
textLabel.setText("Level "  + getLevelName(level) + " - Errors: " + errorCount + "/" + maxErrors);

setupCards();
shuffleCards();
resetBoard(); 
startLevelTimer.start();


                            boardPanel.removeAll();
                            board.clear();

                            int totalCards = cardSet.size();
int gridRows = rows; 
int gridCols = totalCards / gridRows;
if (totalCards % gridRows != 0) gridCols++;  

boardPanel.setLayout(new GridLayout(gridRows, gridCols));


                            for (int i = 0; i < totalCards; i++) {
                                JButton newTile = new JButton();
                                newTile.setPreferredSize(new Dimension(cardWidth, cardHeight));
                                newTile.setOpaque(true);
                                newTile.setIcon(cardSet.get(i).cardImageIcon);
                                newTile.setFocusable(false);
                                newTile.addActionListener(tileClickListener);
                                board.add(newTile);
                                boardPanel.add(newTile);
                            }

                            boardPanel.revalidate();
                            boardPanel.repaint();
                            startLevelTimer.start();
                        }
                    }
                }
            }
        }
    };
}

 //sound methods
void playBackgroundMusic(URL musicURL) {
    try {
        AudioInputStream audioInput =AudioSystem.getAudioInputStream(musicURL);
        backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInput);
            FloatControl bgGain = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            bgGain.setValue(-6.0f); 
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); 
            backgroundClip.start();
        
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

    
    void playSoundEffect(URL soundURL) {
    try {
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInput);
        clip.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
//helper methods
String getLevelName(int level) {
    switch (level) {
        case 1: return "Easy";
        case 2: return "Medium";
        case 3: return "Hard";
        case 4: return "Extreme";
        default: return "Unknown";
    }
}
   
int getPairCountForLevel(int level) {
    switch(level) {
        case 1: return 10;   
        case 2: return 12;  
        case 3: return 14;
        case 4: return 16;
        case 5: return 0;
        default: return 10 ;
    }
}

}

