package unicon.matthews.dataloader.canvas;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.DataLoader;
import unicon.matthews.dataloader.MatthewsClient;
import unicon.matthews.dataloader.canvas.exception.CanvasDataConfigurationException;
import unicon.matthews.dataloader.canvas.exception.UnexpectedApiResponseException;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.reader.ClassReader;

@Component
public class CanvasDataLoader implements DataLoader {
  
  @Value("${downloaddirectory:CANVAS_DUMP}")
  private String downloadDirectory;
  
  @Autowired private MatthewsClient matthewsClient;
  @Autowired private ApiClient apiClient;

  @Override
  public void run() {
    
    try {
      //CanvasDataDump canvasDataDump = apiClient.getLatestDump();
      //canvasDataDump.downloadAllFiles(new File(downloadDirectory));
      
      ClassReader courseSectionReader = new ClassReader(downloadDirectory);
      Collection<unicon.matthews.oneroster.Class> classes = courseSectionReader.read();
      if (classes != null) {
        for (unicon.matthews.oneroster.Class klass : classes) {
          matthewsClient.postClass(klass);
        }
      }
    } 
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 

  }

}
