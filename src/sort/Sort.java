package sort;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Sort{

  int[] numeros;

  public Sort(String archivo, int framerate, String metodo){
    EventQueue.invokeLater(new Runnable(){
      @Override
      public void run(){
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Ordenamientos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new Contenedor(archivo, framerate, metodo));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
      }catch(Exception e){
        System.out.println("\t:(");
      }
      }
    });
  }

  public class Contenedor extends JPanel{

    private JLabel etiqueta;

    public Contenedor(String archivo, int framerate, String metodo){
      setLayout(new BorderLayout());
      etiqueta = new JLabel(new ImageIcon(createImage(archivo)));
      add(etiqueta);
      JButton botonOrdenar = new JButton("Ordenar");
      add(botonOrdenar, BorderLayout.SOUTH);
      botonOrdenar.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
          BufferedImage imagen = (BufferedImage) ((ImageIcon) etiqueta.getIcon()).getImage();
          new UpdateWorker(imagen, etiqueta, archivo, framerate, metodo).execute();
        }
      });

    }

    public BufferedImage createImage(String archivo){
      BufferedImage imagen = null;
      try{
        imagen = ImageIO.read(new File("resource/"+archivo));
        ataqueHackerman(imagen);
        Graphics2D g = imagen.createGraphics();
        g.dispose();
      }catch(Exception e){
        System.err.println("(-)\tAsegurate de estar en el directorio 'src'");
        System.err.println("\ty de haber escrito bien el nombre de imagen (la cual debe estar en la carpeta resource)");
      }
      return imagen;
    }

    public void ataqueHackerman(BufferedImage imagen){
      int length = imagen.getHeight()*imagen.getWidth();
      numeros = new int[length];
      for(int i = 0; i < numeros.length; i++)
        numeros[i] = i;
      Random r = new Random();
      for(int i = 0; i < length; i++){
        int j = r.nextInt(length);
        swapImagen(imagen, i, j);
      }
    }

    public void swapImagen(BufferedImage imagen, int i, int j){
      int colI = i%imagen.getWidth();
      int renI = i/imagen.getWidth();
      int colJ = j%imagen.getWidth();
      int renJ = j/imagen.getWidth();
      int aux = imagen.getRGB(colI, renI);
      imagen.setRGB(colI, renI, imagen.getRGB(colJ, renJ));
      imagen.setRGB(colJ, renJ, aux);
      aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }

  }

  public class UpdateWorker extends SwingWorker<BufferedImage, BufferedImage>{

    private BufferedImage referencia;
    private BufferedImage copia;
    private JLabel target;
    int framerate;
    int n;
    String metodo;
    int iteracion;

    public UpdateWorker(BufferedImage master, JLabel target, String archivo, int speed, String algoritmo){
      this.target = target;
      try{
        referencia = ImageIO.read(new File("resource/"+archivo));
        copia = master;
        n = copia.getHeight()*copia.getWidth();
      }catch(Exception e){
        System.err.println(":c Esto no deberia ocurrir");
      }
      framerate = speed; // Indica cada cuantas iteraciones se actualizara la imagen
      metodo = algoritmo;
      iteracion = 0;
    }

    public BufferedImage updateImage(){
      Graphics2D g = copia.createGraphics();
      g.drawImage(copia, 0, 0, null);
      g.dispose();
      return copia;
    }

    @Override
    protected void process(List<BufferedImage> chunks){
      target.setIcon(new ImageIcon(chunks.get(chunks.size() - 1)));
    }

    public void update(){
      for(int i = 0; i < n; i++){
        int indiceDeOriginal = numeros[i];
        int colOriginal = indiceDeOriginal%copia.getWidth();
        int renOriginal = indiceDeOriginal/copia.getWidth();
        int colI = i%copia.getWidth();
        int renI = i/copia.getWidth();
        copia.setRGB(colI, renI, referencia.getRGB(colOriginal, renOriginal));
      }
      publish(updateImage());
    }

    @Override
    protected BufferedImage doInBackground() throws Exception{
      if(metodo.equals("bubble"))
        bubbleSort();
      if(metodo.equals("selection"))
        selectionSort();
      if(metodo.equals("insertion"))
        insertionSort();
      if(metodo.equals("merge"))
        mergeSort();
      if(metodo.equals("quick"))
        quickSort();
      update();
      return null;
    }

    private void bubbleSort(){
      for(int i = 0; i < n-1; i++){
        for(int j = 0; j < n-i-1; j++){
          if(numeros[j] > numeros[j+1])
          swap(j, j+1);
        }
        if(iteracion%framerate == 0) update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones deseadas
        iteracion = (iteracion+1)%framerate; // Aumentamos el numero de iteraciones
      }
    }

    private void selectionSort(){
      for (int i = 0; i < numeros.length; i++) {
        int min = i; // Menor elemento
        int temp = 0;
        for (int j = i+1; j < numeros.length; j++) {
          temp = j;
          if (numeros[temp] < numeros[min])
            min = temp;
        }
        swap(i, min); // intercambia al mínimo con el de la posición i
        if(iteracion%framerate==0) update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones deseadas
        iteracion = (iteracion+1) % framerate; // Aumentamos el numero de iteraciones
      };
    }

    private void insertionSort(){
      // int n = numeros.length;
      for (int i = 1; i < numeros.length; ++i) {
        int temp = numeros[i];
        int j = i - 1;
        while (j >= 0 && numeros[j] > temp) {
          numeros[j + 1] = numeros[j];
          j = j - 1;
        }
        if(iteracion%framerate==0) update();
        iteracion = (iteracion+1) % framerate;
        numeros[j + 1] = temp;
      }
    }


    private void mergeSort(){
      int n = numeros.length - 1;
      divideMezcla(numeros, 0, n);
    }

    //divide_y_mezcla
    void divideMezcla(int arr[], int izq, int der) {
      int mitad = 0;
      if (izq < der) {
        mitad = (izq + der)/2;
        divideMezcla(arr, izq, mitad);
        divideMezcla(arr , mitad+1 , der);
        mezcla(arr, izq, mitad, der); // mezcla los subarreglos
      }
    }

    void mezcla(int arr[], int izq, int medio, int der) { // Mezcla
      // Recibimos una sola secuencia pero conocemos los segmentos a traves de los 3 int
      int longitudIzq = medio - izq + 1;
      int longitudDer = der - medio; // 
      int izqMerge[] = new int [longitudIzq];
      int derMerge[] = new int [longitudDer]; // Creacion de arreglos auxiliares

      for (int i=0; i<longitudIzq; ++i){ // MUeve los datos de la primera parte de nuestro arreglo auxiliar.
        izqMerge[i] = arr[izq + i];
      }

      for (int j=0; j<longitudDer; ++j){ // MUeve los datos de la segunda parte de nuestro arreglo auxiliar.
        derMerge[j] = arr[medio + 1+ j];
      }

      int i = 0, j = 0;
      int k = izq;
      while (i < longitudIzq && j < longitudDer) { // izq2 = longitudIzq y Der2 = longitudDer
        if (izqMerge[i] <= derMerge[j]) {
          arr[k] = izqMerge[i];
          i++;
          k++;
        }
        else{
          arr[k] = derMerge[j];
          j++;
          k++;
        }
      }
      if(iteracion%framerate==0) update();// Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones deseadas
      iteracion = (iteracion+1) % framerate;

      while (i < longitudIzq){ // mueve el resto de los elementos a nuesto arreglo arr.
        arr[k] = izqMerge[i]; 
        i++;
        k++;
      }

      while (j < longitudDer){
        arr[k] = derMerge[j];
        j++;
        k++;
      }
    }    

    private void quickSort(){
      int n = numeros.length - 1;
      quickSort(numeros, 0, n);
    }

    void quickSort(int[] arr, int minIndice, int maxIndice){ // minIndice=a y maxIndice=b
      int j = 0;
      if (minIndice < maxIndice) {
        j = partition(arr, minIndice, maxIndice); // particiona el arreglo
        quickSort(arr, minIndice, j - 1);
        quickSort(arr, j + 1, maxIndice);
      }
    }

    private int partition(int[] arr, int limIzq, int limDer){
      int i = (limIzq - 1);
      int piv = arr[limDer];
      for(int j = limIzq; j <= limDer - 1; j++){
        if (arr[j] < piv){
          i++;
          swap(i, j);
        }
      }
      swap(i + 1, limDer);
      if(iteracion%framerate==0) update();// Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones deseadas
      iteracion = (iteracion+1) % framerate;
      int corte = i+1;
      return (corte);
    }

    public void swap(int i, int j){
      int aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }

  }

}
