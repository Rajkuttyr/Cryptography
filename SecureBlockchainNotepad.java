import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SecureBlockchainNotepad {

    // Block class
    static class Block {
        String data;
        String previousHash;
        String hash;
        LocalDateTime timestamp;

        Block(String data, String previousHash) {
            this.data = data;
            this.previousHash = previousHash;
            this.timestamp = LocalDateTime.now();
            this.hash = calculateHash();
        }

        String calculateHash() {
            String input = previousHash + timestamp.toString() + data;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < hashBytes.length; i++) {
                    String hex = Integer.toHexString(0xff & hashBytes[i]);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Blockchain class
    static class Blockchain {
        List<Block> chain;

        Blockchain() {
            chain = new ArrayList<Block>();
            chain.add(new Block("Genesis Block", "0"));
        }

        void addNote(String data) {
            Block lastBlock = chain.get(chain.size() - 1);
            chain.add(new Block(data, lastBlock.hash));
            System.out.println("Note added securely to blockchain.");
        }

        boolean isChainValid() {
            for (int i = 1; i < chain.size(); i++) {
                Block current = chain.get(i);
                Block previous = chain.get(i - 1);
                if (!current.hash.equals(current.calculateHash())) {
                    return false;
                }
                if (!current.previousHash.equals(previous.hash)) {
                    return false;
                }
            }
            return true;
        }

        void printChain() {
            for (int i = 0; i < chain.size(); i++) {
                Block block = chain.get(i);
                System.out.println("Note: " + block.data);
                System.out.println("Timestamp: " + block.timestamp);
                System.out.println("Hash: " + block.hash);
                System.out.println("Previous Hash: " + block.previousHash);
                System.out.println("--------------------------------------------------");
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Blockchain notepad = new Blockchain();
        int choice = 0;

        System.out.println("Secure Blockchain Notepad");

        while (choice != 4) {
            System.out.println("\n1. Add Note");
            System.out.println("2. View Notes");
            System.out.println("3. Verify Integrity");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                choice = 0;
            }

            if (choice == 1) {
                System.out.print("Enter your note: ");
                String note = scanner.nextLine();
                notepad.addNote(note);
            } else if (choice == 2) {
                notepad.printChain();
            } else if (choice == 3) {
                if (notepad.isChainValid()) {
                    System.out.println("Blockchain is valid. No tampering detected.");
                } else {
                    System.out.println("Blockchain integrity compromised.");
                }
            } else if (choice == 4) {
                System.out.println("Exiting Secure Notepad.");
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }
}