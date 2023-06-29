import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ContactManagerGUI extends JFrame {

    private JTextField nameField;
    private JTextField numberField;
    private JTextArea displayArea;

    public ContactManagerGUI() {
        setTitle("Contact Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Panel para el formulario de creación
        JPanel createPanel = new JPanel(new GridLayout(3, 2));
        createPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        createPanel.add(nameField);
        createPanel.add(new JLabel("Number:"));
        numberField = new JTextField();
        createPanel.add(numberField);
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new CreateButtonListener());
        createPanel.add(createButton);

        // Panel para mostrar los contactos
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayArea);

        // Panel para el formulario de actualización y eliminación
        JPanel updateDeletePanel = new JPanel(new GridLayout(2, 2));
        updateDeletePanel.add(new JLabel("Name:"));
        JTextField updateDeleteNameField = new JTextField();
        updateDeletePanel.add(updateDeleteNameField);
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new UpdateButtonListener(updateDeleteNameField));
        updateDeletePanel.add(updateButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteButtonListener(updateDeleteNameField));
        updateDeletePanel.add(deleteButton);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createPanel, BorderLayout.NORTH);
        mainPanel.add(displayScrollPane, BorderLayout.CENTER);
        mainPanel.add(updateDeletePanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void displayContacts() {
        try {
            File file = new File("friendsContact.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder contactsBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contactsBuilder.append(line).append("\n");
                }
                reader.close();
                displayArea.setText(contactsBuilder.toString());
            } else {
                displayArea.setText("No contacts found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CreateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String number = numberField.getText();

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("friendsContact.txt", true));
                writer.write(name + "!" + number);
                writer.newLine();
                writer.close();
                displayContacts();
                nameField.setText("");
                numberField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {
        private JTextField nameField;

        public UpdateButtonListener(JTextField nameField) {
            this.nameField = nameField;
        }

        public void actionPerformed(ActionEvent e) {
            String nameToUpdate = nameField.getText();

            try {
                File file = new File("friendsContact.txt");
                File tempFile = new File("temp.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("!");
                    String name = parts[0];
                    String number = parts[1];
                    if (name.equals(nameToUpdate)) {
                        // Actualizar el número del contacto
                        String newNumber = JOptionPane.showInputDialog(null, "Enter new number:");
                        line = name + "!" + newNumber;
                    }
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
                writer.close();

                // Reemplazar el archivo original con el archivo temporal
                file.delete();
                tempFile.renameTo(file);

                displayContacts();
                nameField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        private JTextField nameField;

        public DeleteButtonListener(JTextField nameField) {
            this.nameField = nameField;
        }

        public void actionPerformed(ActionEvent e) {
            String nameToDelete = nameField.getText();

            try {
                File file = new File("friendsContact.txt");
                File tempFile = new File("temp.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                String line;
                boolean deleted = false;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("!");
                    String name = parts[0];
                    if (name.equals(nameToDelete)) {
                        // Saltar la línea del contacto a eliminar
                        deleted = true;
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
                writer.close();

                if (deleted) {
                    // Reemplazar el archivo original con el archivo temporal
                    file.delete();
                    tempFile.renameTo(file);
                    displayContacts();
                    nameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Contact not found.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContactManagerGUI contactManager = new ContactManagerGUI();
                contactManager.displayContacts();
                contactManager.setVisible(true);
            }
        });
    }
}




