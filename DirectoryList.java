import java.io.File;
import java.util.Scanner;

/**
 * This program lists the files in a directory specified by the user. The user
 * is asked to type in a directory name. If the name entered by the user is not
 * a directory, a message is printed and the program ends.
 */
public class DirectoryList {

   String directoryName; // Directory name entered by the user.
   File directory; // File object referring to the directory.
   String[] files; // Array of file names in the directory.
   static Scanner scanner; // For reading a line of input from the user.

   DirectoryList(String fileName) {
      this.directoryName = fileName;
   }

   public static void main(String[] args) {
      scanner = new Scanner(System.in); // scanner reads from standard input.
      System.out.print("Enter a directory name: ");
      String input = scanner.nextLine().trim();
      DirectoryList dl = new DirectoryList(input);
      dl.walk();

   } // end main()

   private void walk() {
      this.directory = new File(this.directoryName);

      if (this.directory.isDirectory() == false) {
         if (this.directory.exists() == false) {
            /**
             * first base case, where the recursion function should return if it reaches a dead end, or dead start.
             * this case is useful when you start with a non existent directory.
             */
            System.out.println("There is no such directory! " + this.directory);
            return;
         } else {
            /**
             * The second base case, if the current directory is a file, just print the file name then return to upper level flow.
             */
            System.out.println("   " + this.directoryName);
            return;
         }
      } else {
         /**
          * Recursion body, if you face a directory, list all files in this directory, then walk() each of them separately.
          */
         this.files = directory.list();
         System.out.println("Files in directory \"" + directory + "\":");
         for (String f : this.files) {
            /**
             * it's important, to append the current directoryName to each of the files name, so you can get the relative path to the file/directory.
             * otherwise it will be considered as absolute path, and it's very rare that your file resides in the /home directory.
             */
            DirectoryList dl = new DirectoryList(this.directoryName + "/" + f);
            dl.walk();
         }
      }
   }

} // end class DirectoryList
