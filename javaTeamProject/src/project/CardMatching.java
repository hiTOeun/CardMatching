package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import match.MatchCards;
import match.ScoreManager;
import matchN.MatchName;
import matchN.ScoreManagerN;
import number.MatchSequence;
import number.ScoreManagerS;

public class CardMatching extends JFrame {
    private final String GAME1_SCORE_FILE = "./game1.txt";
    private final String GAME2_SCORE_FILE = "./game2.txt";

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ArrayList<PlayerScore> game1Scores = new ArrayList<>();
    private ArrayList<PlayerScore> game2Scores = new ArrayList<>();
    private ArrayList<PlayerScore2> game3Scores = new ArrayList<>();
    private MatchCards matchCards;
    private MatchName matchName;

    private String playerName;

    public CardMatching() {
        setTitle("카드 매칭 게임");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 점수 파일 로드
        loadScoresFromFile(GAME1_SCORE_FILE, game1Scores);
        loadScoresFromFile(GAME2_SCORE_FILE, game2Scores);

        playerName = JOptionPane.showInputDialog(CardMatching.this, "이름을 입력하세요:", "플레이어 이름", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Unknown";
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        ImageIcon icon = new ImageIcon(CardMatching.class.getResource("/resource/main/background.png"));
        ImagePanel menuPanel = new ImagePanel(icon.getImage());

        menuPanel.setLayout(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton gameButton = new JButton("게임 시작");
        JButton rankButton = new JButton("등수 확인");
        JButton exitButton = new JButton("게임 종료");

        ImageIcon startIcon = new ImageIcon(CardMatching.class.getResource("/resource/main/start.png"));
        gameButton.setIcon(new ImageIcon(startIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));

        ImageIcon rankIcon = new ImageIcon(CardMatching.class.getResource("/resource/main/rank.png"));
        rankButton.setIcon(new ImageIcon(rankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));

        ImageIcon exitIcon = new ImageIcon(CardMatching.class.getResource("/resource/main/exit.png"));
        exitButton.setIcon(new ImageIcon(exitIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));

        gameButton.setPreferredSize(new Dimension(120, 50));
        rankButton.setPreferredSize(new Dimension(120, 50));
        exitButton.setPreferredSize(new Dimension(120, 50));

        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "gameSelect");
            }
        });

        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "rankSelect");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(gameButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(exitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        menuPanel.add(buttonPanel, gbc);

        JPanel gameSelectPanel = createGameSelectPanel();
        JPanel rankSelectPanel = createRankSelectPanel();

        JPanel game1RankPanel = createRankPanel("게임 1 등수", game1Scores);
        JPanel game2RankPanel = createRankPanel("게임 2 등수", game2Scores); // 게임 2의 랭킹 패널 생성


        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gameSelectPanel, "gameSelect");
        mainPanel.add(rankSelectPanel, "rankSelect");
        mainPanel.add(game1RankPanel, "game1Rank");
        mainPanel.add(game2RankPanel, "game2Rank"); // 게임 2 랭킹 패널 추가

        add(mainPanel);
        cardLayout.show(mainPanel, "menu");

        // 프로그램 종료 시 점수 저장
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveScoresToFile(GAME1_SCORE_FILE, game1Scores);
            saveScoresToFile(GAME2_SCORE_FILE, game2Scores);
        }));
    }

    private void loadScoresFromFile(String fileName, ArrayList<PlayerScore> scores) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new PlayerScore(name, score));
                }
            }
        } catch (IOException e) {
            System.out.println("파일을 불러오는 중 오류가 발생했습니다: " + fileName);
        }
    }

    private void saveScoresToFile(String fileName, ArrayList<PlayerScore> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (PlayerScore score : scores) {
                writer.write(score.getName() + "," + score.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("파일을 저장하는 중 오류가 발생했습니다: " + fileName);
        }
    }

    private JPanel createGameSelectPanel() {
        ImageIcon icon = new ImageIcon(CardMatching.class.getResource("/resource/card2.jpg"));
        ImagePanel gameSelectPanel = new ImagePanel(icon.getImage());
        gameSelectPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1Button = new JButton("게임 1");
        JButton game2Button = new JButton("게임 2");
        JButton game3Button = new JButton("게임 3");
        JButton backButton = new JButton("메뉴로 돌아가기");

        ImageIcon game1Icon = new ImageIcon(CardMatching.class.getResource("/resource/select/game1.png"));
        game1Button.setIcon(new ImageIcon(game1Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2Icon = new ImageIcon(CardMatching.class.getResource("/resource/select/game2.png"));
        game2Button.setIcon(new ImageIcon(game2Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game3Icon = new ImageIcon(CardMatching.class.getResource("/resource/select/game3.png"));
        game3Button.setIcon(new ImageIcon(game3Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game3Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon menuButtonIcon = new ImageIcon(CardMatching.class.getResource("/resource/menu.png"));
        backButton.setIcon(new ImageIcon(menuButtonIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        backButton.setPreferredSize(new Dimension(200, 40));

        game1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchMatchColorGame();
            }
        });

        game2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchMatchNameGame();
            }
        });

        game3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchNumberGame();
            }
        });

        gbc.gridy = 0;
        gameSelectPanel.add(game1Button, gbc);
        gbc.gridy = 1;
        gameSelectPanel.add(game2Button, gbc);
        gbc.gridy = 2;
        gameSelectPanel.add(game3Button, gbc);
        gbc.gridy = 3;
        gameSelectPanel.add(backButton, gbc);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "menu");
            }
        });

        return gameSelectPanel;
    }

    private JPanel createRankSelectPanel() {
        ImageIcon icon = new ImageIcon(CardMatching.class.getResource("/resource/rank.jpg"));
        ImagePanel rankSelectPanel = new ImagePanel(icon.getImage());
        rankSelectPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1RankButton = new JButton("게임 1 등수");
        JButton game2RankButton = new JButton("게임 2 등수");
        JButton backButton = new JButton("메뉴로 돌아가기");

        ImageIcon game1RankIcon = new ImageIcon(CardMatching.class.getResource("/resource/ranking/game1.png"));
        game1RankButton.setIcon(new ImageIcon(game1RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2RankIcon = new ImageIcon(CardMatching.class.getResource("/resource/ranking/game2.png"));
        game2RankButton.setIcon(new ImageIcon(game2RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon menuButtonIcon = new ImageIcon(CardMatching.class.getResource("/resource/menu.png"));
        backButton.setIcon(new ImageIcon(menuButtonIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        backButton.setPreferredSize(new Dimension(200, 50));

        game1RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game1Rank"));
        game2RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game2Rank"));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        rankSelectPanel.add(game1RankButton, gbc);

        gbc.gridy = 1;
        rankSelectPanel.add(game2RankButton, gbc);

        gbc.gridy = 2;
        rankSelectPanel.add(backButton, gbc);

        return rankSelectPanel;
    }

    private JPanel createRankPanel(String title, ArrayList<PlayerScore> scores) {
        JPanel rankPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        rankPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea rankTextArea = new JTextArea(10, 30);
        rankTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(rankTextArea);
        rankPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("랭크 선택 화면으로 돌아가기");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        rankPanel.add(backButton, BorderLayout.SOUTH);

        rankPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                updateRankArea(rankTextArea, scores);
            }
        });

        return rankPanel;
    }

    private void updateRankArea(JTextArea rankArea, ArrayList<PlayerScore> scores) {
        Collections.sort(scores, Comparator.comparingInt(PlayerScore::getScore).reversed());
        StringBuilder rankText = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            rankText.append(i + 1).append(". ").append(scores.get(i).getName())
                    .append(" - ").append(scores.get(i).getScore()).append("\n");
        }
        rankArea.setText(rankText.toString());
    }

    private void launchMatchColorGame() {
        ScoreManager scoreManager = new ScoreManager();
        matchCards = new MatchCards(scoreManager);
        matchCards.setGameEndListener(new MatchCards.GameEndListener() {
            @Override
            public void onGameEnd(int finalScore) {
                game1Scores.add(new PlayerScore(playerName, finalScore)); // 입력 없이 자동 저장
                JOptionPane.showMessageDialog(CardMatching.this, "점수가 저장되었습니다!");
                cardLayout.show(mainPanel, "menu");
            }
        });
        matchCards.run();
    }


    private void launchMatchNameGame() {
        ScoreManagerN scoreManagerN = new ScoreManagerN();
        matchName = new MatchName(scoreManagerN);
        matchName.setGameEndListener(new MatchName.GameEndListener() {
            @Override
            public void onGameEnd(int finalScore) {
                game2Scores.add(new PlayerScore(playerName, finalScore)); // 입력 없이 자동 저장
                JOptionPane.showMessageDialog(CardMatching.this, "점수가 저장되었습니다!");
                cardLayout.show(mainPanel, "menu");
            }
        });
        matchName.run();
    }


//    private void saveGame3Score(String playerName, int matchedCards, int remainingTime) {
//        if (playerName != null && !playerName.trim().isEmpty()) {
//            game3Scores.add(new PlayerScore2(playerName, matchedCards, remainingTime));
//            JOptionPane.showMessageDialog(this, "점수가 저장되었습니다!");
//        }
//    }
//
//    private void showGame3Rankings() {
//        game3Scores.sort((o1, o2) -> {
//            if (o2.getMatchedCards() != o1.getMatchedCards()) {
//                return Integer.compare(o2.getMatchedCards(), o1.getMatchedCards());
//            }
//            return Integer.compare(o2.getRemainingTime(), o1.getRemainingTime());
//        });
//
//        StringBuilder sb = new StringBuilder("Game 3 Rankings:\n");
//        for (int i = 0; i < game3Scores.size(); i++) {
//            sb.append(i + 1).append(". ").append(game3Scores.get(i).toString()).append("\n");
//        }
//        JOptionPane.showMessageDialog(this, sb.toString(), "Game 3 Rankings", JOptionPane.INFORMATION_MESSAGE);
//    }

    private void launchNumberGame() {
        ScoreManagerS scoreManager = new ScoreManagerS();
        MatchSequence matchSequence = new MatchSequence(scoreManager);

        matchSequence.setGameEndListener((matchedCards, remainingTime) -> {
            game3Scores.add(new PlayerScore2(playerName, matchedCards, remainingTime)); // 입력 없이 자동 저장
            JOptionPane.showMessageDialog(this, "점수가 저장되었습니다!");
            cardLayout.show(mainPanel, "menu");
        });

        matchSequence.run();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardMatching().setVisible(true));
    }
}