import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.io.IOException;
import java.util.List;
import java.io.FileWriter;

public class ScrapeCourseList {
    public static void main(String[] args) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        try {
            HtmlPage page = webClient.getPage("https://schulich.ucalgary.ca/future-students/undergraduate/programs/common-first-year");

            String title = page.getTitleText();
            System.out.println("Page Title: " + title);

            List<HtmlElement> fullCourses = page.getByXPath("//div[@class=\"text-chunk\"]/h4");

            FileWriter coursesFile = new FileWriter("course_data.csv", false);
            coursesFile.write("course_id,course_name,course_number\n");

            for (int i = 0; i < fullCourses.size(); i++) {
                String fullName = fullCourses.get(i).asNormalizedText();
                if (fullName.contains("optional")) {
                    continue;
                }
                String fullCourseName = fullName.substring(fullName.indexOf("(")+1, fullName.indexOf(")"));
                String courseName = fullCourseName.substring(0, fullCourseName.indexOf(" "));
                courseName = courseName.toUpperCase();
                String courseNum = fullCourseName.substring(fullCourseName.indexOf(" ") + 1);
                coursesFile.write(i + 1 + "," + courseName + "," + courseNum + "\n");
            }

            coursesFile.close();

            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
}
