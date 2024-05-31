import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OyunEkrani extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JList<String> characterList;

    private String[] characterNames = {"Alex","Alfred","Bernard","Bill","Charles","David","Eric","Frans","George","Herman","Joe","Max","Paul","Peter","Philip","Richard","Robert","Sam","Tom"};
    private boolean[][] characterAttributes = {
            {true,true , false, false, true , false},  // Alex
            {true,false, false, false, true , false},  // Alfred
            {true,false, false, false, false, false},  // Bernard
            {true,false, false, true , true , false},  // Bill
            {true,false, false, false, true , false},  // Charles
            {true,false, false, false, true , false},  // David
            {true,false, false, false, false, false},  // Eric
            {true,false, false, false, false, false},  // Frans
            {true,false, false, false, false, true },  // George
            {true,false, false, true , false, true },  // Herman
            {true,false, true , false, false, false},  // Joe
            {true,true , false, false, true , false},  // Max
            {true,false, true , false, false, true },  // Paul
            {true,false, false, false, false, true },  // Peter
            {true,true , false, false, true , false},  // Philip
            {true,false, false, true , true , false},  // Richard
            {true,false, false, false, false, false},  // Robert
            {true,false, true , true , false, true },  // Sam
            {true,true , true , true , false, true }   // Tom
            
    };

    private String selectedCharacter;
    private CharacterData selectedCharacterData;
    private List<String> remainingCharacters;

    private String[] questions = {"","Saç Rengi Siyah mı?","Gözlük Takıyor mu?","Kel mi?","Bıyığı/Sakalı var mı?","Yaşlı mı?"};

    public OyunEkrani() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 820, 950);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        for (int i = 0; i < characterNames.length; i++) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon("images/" + characterNames[i] + ".png"));
            label.setPreferredSize(new Dimension(110, 170));
            label.setBounds(10 + (i % 5) * 120, 11 + (i / 5) * 190, 110, 170);
            contentPane.add(label);
        }

        startGame(); // Oyuna başla
        askRandomQuestions(); // Rastgele soruları ekrana yerleştir ve kullanıcının cevaplamasını bekle
    }

    private void startGame() {
        // Ekranda kalan karakterleri başlangıçta tüm karakterlerle doldur
        remainingCharacters = new ArrayList<>();
        for (String characterName : characterNames) {
            remainingCharacters.add(characterName);
        }

        // Rastgele bir karakter seç
        Random random = new Random();
        int randomIndex = random.nextInt(remainingCharacters.size()); // Use remainingCharacters.size() instead of characterNames.length
        selectedCharacter = remainingCharacters.get(randomIndex);
        selectedCharacterData = new CharacterData(characterAttributes[getCharacterIndex(selectedCharacter)]);
    }

    private void askRandomQuestions() {
        Random random = new Random();

        // 5 sorudan rastgele 3 tanesini seç
        final int[] selectedQuestionIndices = new int[3];
        for (int i = 0; i < 3; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(questions.length); // 0'dan 4'e kadar rastgele indeks
            } while (contains(selectedQuestionIndices, randomIndex));
            selectedQuestionIndices[i] = randomIndex;

            // Seçilen soruyu ekrana yerleştir
            String question = getQuestionByIndex(randomIndex);
            JButton questionButton = new JButton(question);
            questionButton.setBounds(50 + i * 160, 788, 150, 23);
            int finalRandomIndex = randomIndex;
            questionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkAnswer(finalRandomIndex, question);
                    removeCharacterLabels(finalRandomIndex);
                }
            });
            contentPane.add(questionButton);
        }

        // Tahmin yapma butonu ekleyerek tahmin listesini göster
        JButton guessButton = new JButton("Tahmin Yap");
        guessButton.setBounds(631, 366, 150, 23);
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makePrediction();
            }
        });
        contentPane.add(guessButton);

        // Pencerenin sağ tarafında kalan karakterleri gösteren bir liste ekle
        characterList = new JList<>();
        characterList.setBounds(631, 11, 150, 344);
        contentPane.add(characterList);
        updateCharacterList();
    }

    private void updateCharacterList() {
        // Karakter listesini güncelle
        String[] characterArray = new String[remainingCharacters.size()];
        characterList.setListData(remainingCharacters.toArray(characterArray));
    }

    private void removeCharacterLabels(int attributeIndex) {
        // Ekranda kalan karakterleri kontrol et
        List<String> charactersToRemove = new ArrayList<>();
        for (String characterName : remainingCharacters) {
            int characterIndex = getCharacterIndex(characterName);

            // Güncelleme: characterIndex'nin geçerli olup olmadığını kontrol et
            if (characterIndex >= 0 && characterIndex < characterAttributes.length) {
                boolean isAttributeTrueForCharacter = characterAttributes[characterIndex][attributeIndex];
                boolean isAttributeTrueForSelectedCharacter = selectedCharacterData.getAnswers()[attributeIndex];

                // Eğer karakter, seçilen sorunun cevabına uygun değilse, karakteri listeden kaldır
                if (isAttributeTrueForCharacter != isAttributeTrueForSelectedCharacter) {
                    charactersToRemove.add(characterName);
                }
            }
        }

        // Kaldırılacak karakterleri listeden kaldır
        remainingCharacters.removeAll(charactersToRemove);

        // Güncelleme: Yeni bir karakter seç sadece kalan karakterler varsa
        if (!remainingCharacters.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(remainingCharacters.size());
            selectedCharacter = remainingCharacters.get(randomIndex);
            selectedCharacterData = new CharacterData(characterAttributes[getCharacterIndex(selectedCharacter)]);
        }

        // Ekranda sadece kalan karakterleri göster
        List<Component> componentsToRemove = new ArrayList<>();
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                String labelText = ((JLabel) component).getIcon().toString();
                String characterName = getCharacterNameFromLabel(labelText);

                // Güncelleme: Sadece kalan karakterlerin etiketlerini kaldır
                if (!remainingCharacters.contains(characterName)) {
                    componentsToRemove.add(component);
                }
            }
        }

        // Kaldırılacak etiketleri listeden kaldır
        for (Component component : componentsToRemove) {
            contentPane.remove(component);
        }

        contentPane.revalidate();
        contentPane.repaint();

        updateCharacterList();
    }



    private void makePrediction() {
        // Debugging statements
        System.out.println("Remaining Characters: " + remainingCharacters);
        System.out.println("Selected Character: " + selectedCharacter);

        // Güncelleme: Seçili karakteri al
        String selectedCharacterFromList = characterList.getSelectedValue();

        // Güncelleme: Tahminin doğruluğunu kontrol et
        if (selectedCharacterFromList != null) {
            boolean isPredictionCorrect = selectedCharacterFromList.equalsIgnoreCase(selectedCharacter);
            System.out.println("Selected Character from List: " + selectedCharacterFromList);
            // Debugging statement
            System.out.println("Is Prediction Correct? " + isPredictionCorrect);

            // Kullanıcıya sonucu göster
            if (isPredictionCorrect) {
                String correctPrediction = selectedCharacterFromList;
                
                JOptionPane.showMessageDialog(contentPane, "Doğru tahmin! " + correctPrediction + " Oyunu kazandınız.", "Tebrikler!", JOptionPane.INFORMATION_MESSAGE);

                // Oyunu kapat
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(contentPane, "Üzgünüz, yanlış tahmin. Doğru cevap: " + selectedCharacter, "Üzgünüz!", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            // Kullanıcı bir karakter seçmediyse uyarı ver
            JOptionPane.showMessageDialog(contentPane, "Lütfen bir karakter seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }



    private String getCharacterNameFromLabel(String labelText) {
        // Etiketin içinden karakter adını çıkart
        for (String characterName : characterNames) {
            if (labelText.contains(characterName)) {
                return characterName;
            }
        }
        return "";
    }

    private int getCharacterIndex(String characterName) {
        // Karakter adının indeksini bul
        for (int i = 0; i < characterNames.length; i++) {
            if (characterNames[i].equals(characterName)) {
                return i;
            }
        }
        return -1;
    }

    private String getQuestionByIndex(int index) {
        return questions[index];
    }

    private void checkAnswer(int attributeIndex, String question) {
        try {
            boolean isAttributeTrueForSelectedCharacter = selectedCharacterData.getAnswers()[attributeIndex];

            if (isAttributeTrueForSelectedCharacter) {
                JOptionPane.showMessageDialog(this, "Evet", question, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Hayır", question, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException at attributeIndex: " + attributeIndex);
            e.printStackTrace();
        }
    }

    private boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    private class CharacterData {
        private boolean[] answers;

        public CharacterData(boolean[] answers) {
            this.answers = answers;
        }

        public boolean[] getAnswers() {
            return answers;
        }
    }

    public void showFrame() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OyunEkrani().showFrame();
            }
        });
    }
}
