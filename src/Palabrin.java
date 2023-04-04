import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Palabrín, una acertada combinación del clásico juego del ahorcado y el famosísimo mastermind
 *
 * @author David Ayllón Martín
 * @author Cayetana Rosado Grijalba
 * @version 1.0
 */
public class Palabrin {

    //Constantes que contienen los escapes ANSI para cambiar de color las letras
    final static String ANSI_VOLVER_A_BLANCO = "\u001B[0m";
    final static String ANSI_VERDE = "\u001B[32m";
    final static String ANSI_AMARILLO = "\u001B[33m";
    final static String ANSI_ROJO = "\u001B[31m";
    //Con esta constante podemos establecer el número de intentos que tendrá el juego
    final static int NUMERO_INTENTOS = 10;
    static Scanner read = new Scanner(System.in);

    public static void main(String[] args) {

        //Cargamos un diccionario de palabras
        String[] diccionario = cargarDiccionario();
        //Cargamos una palabra oculta del diccionario para resolverla
        String palabraOculta = elegirPalabraOculta(diccionario);
        //Generamos el tablero de juego
        String[] tablero = generarTablero(NUMERO_INTENTOS);
        //Generamos un array con todas las letras posibles para ir coloreando en función de si aparecen o no en nuestra palabra
        String[] letrasPosibles = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ", "Z", "X", "C", "V", "B", "N", "M"};
        //Un bucle que se repita tantas veces como intentos hayamos establecido
        for (int intento = 0; intento < NUMERO_INTENTOS; intento++) {
            //Pedimos una palabra al usuario
            String palabraIntento = PedirPalabra(diccionario);
            //Limpiamos la pantalla para añadir claridad
            limpiarPantalla();
            //Si el usuario introduce la palabra POKE pedirPista será true y dará una letra que no esté
            //Si la palabra introducida es otra, dará false y procesará dicha palabra
            if (pedirPista(letrasPosibles, palabraOculta, palabraIntento)) {
                //Reducimos intento en uno para que al introducir POKE no cuente
                intento--;
            } else {
                //Procesamos la palabra para darle color, introducirla en el tablero y modificar el array de posibilidades
                tablero[intento] = procesarPalabra(letrasPosibles, palabraOculta, palabraIntento);
            }
            //Imprimimos las letras posibles con su código de color en función de los intentos que hayamos hecho
            imprimirLetrasPosibles(letrasPosibles);
            //Imprimimos el tablero de juego con todos los intentos con su correspondiente código de color
            imprimirTablero(tablero);
            //Comprobamos la condición de final de partida para terminar el bucle
            if (comprobarFinalPartida(palabraOculta, intento, palabraIntento))
                break;
        }
    }

    /**
     * Este método imprime en pantalla una letra de la palabra oculta que el usuario todavía no conozca cuando se introduce la palabra seleccionada para las pistas
     *
     * @param letrasPosibles array que contiene las posibles letras de la palabra oculta
     * @param palabraOculta  palabra oculta que el usuario tiene que adivinar
     * @param palabraIntento palabra que el usuario está utilizando para intentar adivinar la palabra oculta
     * @return valor booleano que será verdadero cuando la palabra introducida sea la establecida para las pistas y falso cuando no lo sea
     */
    public static boolean pedirPista(String[] letrasPosibles, String palabraOculta, String palabraIntento) {
        if (palabraIntento.equals("POKE")) {
            //Con estos dos bucles anidados eliminamos las letras que el usuario ya conoce
            for (int i = 0; i < palabraOculta.length(); i++) {
                for (String letra : letrasPosibles) {
                    //Si la letra está coloreada de verde o de amarillo, el usuario ya la conoce y cambiamos todas las
                    //que haya en la palabra por un espacio
                    if (letra.equals("\u001B[32m" + palabraOculta.charAt(i) + "\u001B[0m") || letra.equals("\u001B[33m" + palabraOculta.charAt(i) + "\u001B[0m")) {
                        palabraOculta = palabraOculta.replace(palabraOculta.charAt(i), ' ');
                    }
                }
            }
            //Eliminamos todos los espacios de la palabra. Después de esto lo que quedará en el String solo serán letras
            //que el usuario no tenga descubiertas
            palabraOculta = palabraOculta.replaceAll(" ", "");

            //Si el String está vacío, no quedan pistas que dar
            if (palabraOculta.equals("")) {
                System.out.println("Ya tienes todas las letras de la palabra");
                //Si no, elegimos una letra al azar de las que hay disponibles, la imprimimos en pantalla y la coloreamos en
                //la matriz de letras posibles
            } else {
                char c = palabraOculta.charAt((int) (Math.random() * palabraOculta.length()));
                System.out.println("La letra " + c + " se encuentra en tu palabra");
                for (int i = 0; i < letrasPosibles.length; i++) {
                    if (letrasPosibles[i].equals(Character.toString(c)))
                        letrasPosibles[i] = "\u001B[33m" + c + "\u001B[0m";
                }
            }
            return true;
        } else
            return false;
    }

    /**
     * Método que lee un fichero con palabras del diccionario
     *
     * @return devuelve un array con palabras del diccionario
     */
    public static String[] cargarDiccionario() {
        // Array de Strings para cargar palabras de 8 caracteres
        String[] diccionario = new String[73252];

        // Utilizaremos un Scanner para cargar el fichero txt
        Scanner sc;
        int i = 0;

        try {
            // Cargamos el fichero txt guardado en la carpeta del proyecto
            sc = new Scanner(new FileReader("ficheros/palabras.txt"));
            String str;

            //repetir hasta terminar de leer el fichero
            while (sc.hasNext()) {
                str = sc.next();

                // añadir palabra al diccionario poniéndola en mayúsculas y sin espacios en blanco
                diccionario[i] = str.trim().toUpperCase();
                i++;
            }


        } catch (FileNotFoundException e) {
            // asegurarse que la ruta y el nombre del fichero son correctos
            System.err.println("Fichero no encontrado");
        }
        return diccionario;
    }

    /**
     * Método que elige una palabra aleatoria de todas las posibilidades que se le ofrezcan en un diccionario de palabras
     *
     * @param diccionario array de palabras del que se extrae la palabra
     * @return palabra aleatoria que funcionará como palabra oculta
     */
    public static String elegirPalabraOculta(String[] diccionario) {
        //Crea un número aleatorio para elegir una palabra al azar del diccionario
        return diccionario[(int) (Math.random() * diccionario.length)];
    }

    /**
     * Método que genera un tablero de juego para Palabrín
     *
     * @param numeroIntentos número de filas que tendrá el tablero
     * @return tablero de juego con cadenas llenas de guiones para indicar los caracteres que tiene que tener cada intento
     */
    public static String[] generarTablero(int numeroIntentos) {
        //Devuelve una array del tamaño del número de intentos que se haya establecido lleno de guiones
        String[] intentos = new String[numeroIntentos];
        for (int i = 0; i < numeroIntentos; i++) {
            intentos[i] = "--------";
        }
        return intentos;
    }

    /**
     * Método que pide al usuario que introduzca una palabra para jugar y que comprueba que esté dentro del diccionario utilizado
     *
     * @param diccionario diccionario para comprobar los intentos del usuario
     * @return palabra que funcionará como intento del usuario
     */
    public static String PedirPalabra(String[] diccionario) {
        //Genera un mensaje en pantalla para darle al usuario información
        System.out.println("Escribe una palabra de 8 letras:");
        //Leemos con un scanner la palabra del usuario, la ponemos en mayúsculas y le quitamos todos los espacios
        String palabraIntento = read.nextLine().toUpperCase().replaceAll(" ", "");
        //Si la palabra no está en el diccionario o no es la palabra para pedir ayuda le pedimos al usuario que introduzca otra
        while (!palabraIntento.equals("POKE") && Arrays.binarySearch(diccionario, palabraIntento) < 0) {
            System.out.println("La palabra no es válida. Introduzca una que sea válida:");
            palabraIntento = read.nextLine().toUpperCase().replaceAll(" ", "");
        }

        return palabraIntento;
    }

    /**
     * Método que recibe la palabra oculta y el intento del usuario, los compara y colorea el intento del usuario y el array de posibilidades con el código de colores del juego
     *
     * @param letrasPosibles array de letras posibles para adivinar la palabra oculta
     * @param palabraOculta  palabra oculta que tiene que adivinar el usuario
     * @param palabraIntento palabra con la que el usuario intenta adivinar la palabra oculta
     * @return intento del usuario coloreado según el código de colores establecido en el juego
     */

    //Somos conscientes de que el código que desarrollamos en clase para colorear la palabra probablemente sea más
    //eficiente, pero hemos decidido mantener nuestro trabajo original
    public static String procesarPalabra(String[] letrasPosibles, String palabraOculta, String palabraIntento) {

        StringBuilder palabraSecreta = new StringBuilder(palabraOculta);

        //Con este bucle recorremos todas las letras de la palabra que queremos comprobar
        for (int i = 0; i < palabraIntento.length(); i++) {
            char letra = palabraIntento.charAt(i);
            //Cuando coincidan en posición, sustituimos la letra en cuestión por un caracter que no pueda aparecer en las palabras
            //Hacemos esto para simplificar la operación de darles color a las letras
            if (palabraSecreta.charAt(i) == letra) {
                palabraSecreta.setCharAt(i, ' ');
            }
        }
        //Definimos un nuevo StringBuilder para ir añadiendo la información de todas las letras ya modificadas
        StringBuilder sb = new StringBuilder();

        //Volvemos a recorrer nuestra palabra para colorear las letras del tablero y del array de letras posibles
        for (int i = 0; i < palabraIntento.length(); i++) {
            char letra = palabraIntento.charAt(i);
            //El espacio se lo hemos asignado a una letra que estaba bien colocada, con lo cual, cuando aparezca,
            //coloreamos de verde la letra. La información de qué letra es la sacamos del intento
            if (palabraSecreta.charAt(i) == ' ') {
                //Concatenamos 3 funciones append para añadir a nuestro StringBuilder el verde, la letra que
                //corresponda y volver al blanco
                sb.append(ANSI_VERDE).append(palabraIntento.charAt(i)).append(ANSI_VOLVER_A_BLANCO);
                //Cuando la letra coincida también tenemos que modificar el array de letras posibles. Lo recorremos
                for (int j = 0; j < letrasPosibles.length; j++) {
                    //Si la letra coincide con alguna de las que hay dentro del array, sin colorear o coloreada de
                    //amarillo, la coloreamos de verde porque ya sabemos que está en la palabra y su posición
                    if (letrasPosibles[j].equals(Character.toString(letra)) || letrasPosibles[j].equals(ANSI_AMARILLO + letra + ANSI_VOLVER_A_BLANCO) || letrasPosibles[j].equals(ANSI_ROJO + letra + ANSI_VOLVER_A_BLANCO)) {
                        letrasPosibles[j] = ANSI_VERDE + letra + ANSI_VOLVER_A_BLANCO;
                        break;
                    }
                }
            /*Si la letra no está en la posición correcta tenemos que comprobar si está dentro de la palabra. Utilizamos
            la función contains para saber si la letra está contenida en nuestra palabra secreta. Hay que tener en
            cuenta que hemos eliminado de la palabra secreta las letras que ya están en una posición correcta, por lo
            tanto, en caso de que solo haya una letra y ya esté localizada no encontrará nada, pero si hubiera varias
            letras iguales el resto las encontraría y las colorearía de amarillo */
            } else if (palabraSecreta.toString().contains(Character.toString(letra))) {
                //Concatenamos 3 funciones append para añadir a nuestro StringBuilder el amarillo, la letra que
                //corresponda y volver al blanco
                sb.append(ANSI_AMARILLO).append(letra).append(ANSI_VOLVER_A_BLANCO);
                //Cuando haya coincidencia también tenemos que modificar el array de letras posibles. Lo recorremos
                for (int j = 0; j < letrasPosibles.length; j++) {
                    //Si la letra sin modificar está dentro del array, la coloreamos de amarillo
                    if (letrasPosibles[j].equals(Character.toString(letra))) {
                        letrasPosibles[j] = ANSI_AMARILLO + letra + ANSI_VOLVER_A_BLANCO;
                        break;
                    }
                }
                //Con este bucle vamos a eliminar de la palabra secreta la primera letra que coincida con la letra que
                //está contenida en la palabra para que en las siguientes iteraciones no la tenga en cuenta si hubiera
                //más. Con esto conseguimos que no siga coloreando de amarillo las próximas coincidencias en caso de que
                //se repitieran
                for (int j = 0; j < palabraIntento.length(); j++) {
                    if (palabraSecreta.charAt(j) == letra) {
                        palabraSecreta.setCharAt(j, '*');
                        break;
                    }
                }
                //Si no está en el lugar correcto ni está contenida en la palabra significa que la letra no está en la
                //palabra, con lo cual añadimos la letra al StringBuilder sin código de color
            } else {
                sb.append(letra);
                //Como la letra no existe en la palabra lo tenemos que indicar en el array de letras posibles. Lo recorremos
                for (int j = 0; j < letrasPosibles.length; j++) {
                    //Si la letra todavía está sin modificar en el array, la modificamos y le añadimos color rojo
                    if (letrasPosibles[j].equals(Character.toString(letra))) {
                        letrasPosibles[j] = ANSI_ROJO + letra + ANSI_VOLVER_A_BLANCO;
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Método que limpia la pantalla para que el usuario acceda a la información con más claridad
     */
    public static void limpiarPantalla() {
        for (int k = 0; k < 100; k++) { //Imprimir varios saltos de línea para no saturar la pantalla
            System.out.println();
        }
    }

    /**
     * Método que imprime en pantalla el array de letras posibles para adivinar la palabra oculta
     *
     * @param letrasPosibles array de letras posibles
     */
    public static void imprimirLetrasPosibles(String[] letrasPosibles) {
        for (int i = 0; i < letrasPosibles.length; i++) {
            System.out.print(letrasPosibles[i] + " ");
            if ((i + 1) % 10 == 0)
                System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    /**
     * Método que imprime el tablero de juego en pantalla
     *
     * @param tablero tablero de juego
     */
    public static void imprimirTablero(String[] tablero) {
        for (String str : tablero) { //Imprimir los intentos que hay acumulados en el array de intentos
            System.out.println(str);
        }
        System.out.println();
    }

    /**
     * Método que comprueba si la partida ha finalizado e imprime en pantalla un mensaje para el usuario cuando lo haya hecho
     *
     * @param palabraOculta  palabra oculta que tiene que adivinar el usuario
     * @param intentos       número de intentos que lleva el usuario
     * @param palabraIntento palabra con la que el usuario intenta adivinar la palabra oculta
     * @return valor booleano verdadero en caso de final de partida y falso en caso de que no
     */
    public static boolean comprobarFinalPartida(String palabraOculta, int intentos, String palabraIntento) {
        boolean terminada = false;
        //Si la palabra oculta coincide con la palabra intento la partida acaba. Imprimimos un mensaje de felicitación y cambiamos terminada a true
        if (palabraIntento.equals(palabraOculta)) {
            System.out.println("¡Felicidades! ¡Has acertado la palabra!");
            imprimirPuntuacion(intentos);
            terminada = true;
            //En el caso de que el intento sea el último y no se haya acertado la palabra la partida también acaba. Imprimimos un mensaje de derrota y mostramos la palabra oculta
        } else if (intentos == (NUMERO_INTENTOS - 1)) {
            System.out.println("Lo siento, no has adivinado la palabra. La palabra era " + palabraOculta);
            System.out.println("Tu puntuación es de 0 puntos");
            terminada = true;
        }
        return terminada;
    }

    /**
     * Método que imprime en pantalla la puntuación obtenida
     *
     * @param intentos número de intentos utilizados para adivinar la palabra
     */
    public static void imprimirPuntuacion(int intentos) {
        int puntuacion = 100 * NUMERO_INTENTOS - 100 * intentos;
        System.out.format("¡Tu puntuación ha sido de %d puntos!", puntuacion);
    }
}