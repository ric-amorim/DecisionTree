
public class DNode<T> {
   T valor;
   Object[] filhos;

   DNode(T v, int numExem) {
      valor = v;
      filhos = new Object[numExem];
   }

   public void addFilho(DNode<T> filho) {
      // Find the first available index in the filhos array
      int index = 0;
      while (index < filhos.length && filhos[index] != null) {
         index++;
      }
      // Add the filho to the array if there is an available index
      if (index < filhos.length) {
         filhos[index] = filho;
      } else {
         // Handle the case where the array is already full
         System.out.println("Cannot add more filhos. Array is full.");
      }
   }
}
