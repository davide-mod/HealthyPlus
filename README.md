# Healthy Plus

Applicazione sviluppata per il progetto di testi "Healthy Plus - Riprogettazione ed evoluzione di un’applicazione per il monitoraggio di sani stili di vita." che si compone per ora di un modulo per il tracking di attività fisica e uno per la gestione dei pasti.

Data la natura open del progetto, di seguito sono riportate le istruzioni per aggiungere un nuovo modulo così come la struttura dei database.

## Database in locale 

Ho fatto una tabella per i pasti salvati di Meal Planner e una per gli allenamenti di Fitness Tracker.

Ogni tabella ha le entry che sono poi l'oggetto rispettivamente di tipo Meal e Workout (che si trovano nei file **Meal.kt** e **Workout.kt**).

```kotlin
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var foodList: String,
    var date: String,
    var ispreset: Boolean,
    var isdone: Boolean
)
```

```kotlin
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var exerciseList: String,
    var date: String,
    var ispreset: Boolean,
    var isdone: Boolean
)
```

Per quanto riguarda il modulo Meal Planner, le query sono nel file **MealDAO.kt**; vengono richiamate all'interno delle coroutine (ovvero dei processi paralleli per non appesantire quello principale) nella classe **MealRepository.kt** che a loro volta vengono richiamate nel progetto usando la **MealSharedViewModel.kt** in modo da avere l'accesso da qualsiasi punto del modulo Meal Planner. Discorso analogo per Fitness Tracker.

Per aggiungere una nuova tabella, una soluzione può essere il copiare la cartella **mealdb** o **workoutdb** andando poi a modificare i necessari nomi.

## Database NoSQL Firebase

Per il backup online dei dati è stato utilizzato il "back-end as service" Firebase con una struttura del tipo:
- raccolta dei documenti utente (ognuno con un userID come chiave):
- - dentro il documento, sono presenti i campi della registrazione (_nome_, _cognome_, _email_ e _data di nascita_) e le raccolte relative ai dati dei moduli:
- - - per ora, i dati presenti sono una lista di **Meal** nella raccolta "meals" e una lista di **Workout** nella lista "workouts".

In geneale la struttura si può semplificare come nell'immagine di seguito:

<img src="https://github.com/davide-mod/HealthyPlus/blob/master/databasescheme.png" width="400" alt="Struttura Firebase">

## Come aggiungere un nuovo modulo

Premessa: ho già inserito nel codice del progetto delle righe commentate di un ipotetico "NuovoModulo"

##### Per aggiungere un nuovo modulo al progetto i passi sono essenzialmente 4:

1. Creare un nuovo package (es. *com.modolo.healthyplus.nuovomodulo*) dove inserire tutti i fragment necessari con i relativi layout nella cartella */res/layout*

2. Aggiungere al file _/res/navigation/_**nav_graph.xml** tutti i fragment che vogliamo utilizzare. Es: 

   ```xml
   <fragment
       android:id="@+id/nuovoModuloFragment"
       tools:layout="@layout/fragment_nuovomodulo"
       android:name="com.modolo.healthyplus.nuovomodulo.NuovoModuloFragment"
       android:label="NuovoModuloFragment" />
   ```

3. Aggiungere al file _/res/menu/_**item_nav_view.xml** l'oggetto che sarà poi selezionabile nel drawer (o hamburger menu). Es: 

   ```xml
   <item
       android:id="@+id/itemNuovoModulo"
       android:icon="@drawable/icon_nuovomodulo"
       android:title="@string/nuovomodulo_title" />
   ```

4. Modificare il file **MainActivity.kt** come segue: nel navigation listener aggiungere, per l'appunto, un nuovo listener: 

   ```kotlin
   R.id.itemNuovoModulo -> { //questo id è quello inserito nel /res/menu/item_nav_view.xml
       findNavController(R.id.nav_host_fragment).navigate(R.id.nuovoModuloFragment)
   }
   ```

##### Se poi si vuole poi rendere il nuovo modulo nascosto o visibile in base ad una "SharedPreference" bisogna effettuare un paio di passaggi extra:

1. Modificare ulteriormente il file **MainActivity.kt** come segue:

   1. aggiungere una nuova preferenza che determinerà la visibilità della voce "Nuovo Modulo" nel drawer: 

      ```kotlin
      private val MOD_NUOVO_MODULO = "nuovo"
      ```

   2. nel metodo `onCreate` leggere la preferenza del punto 1. e impostare la visibilità nel drawer dell'elemento: 

      ```kotlin
      val nuovoModulo = sharedPref?.getBoolean(MOD_NUOVO_MODULO, true)
      setDrawerElementVisible(R.id.itemNuovoModulo, nuovoModulo!!)
      ```

2. Modificare il file _/res/layout/_**framgment_user.xml** aggiungendo la checkbox nel LinearLayout per selezionare la visibilità: 

   ```xml
   <CheckBox
       android:id="@+id/checkNuovoModulo"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:layout_marginStart="@dimen/marginStandard"
       android:layout_marginTop="@dimen/marginStandard"
       android:text="@string/nuovomodulo_title"
       android:textSize="@dimen/buttonText" />
   ```

3. Modificare il file _/userpage/_**UserFragment.kt** come segue:

   1. aggiungere anche qui la preferenza: 

      ```kotlin
      private val MOD_NUOVO_MODULO = "nuovo"
      ```

   2. recuperare il valore e modificare la checkbox del punto 2. (all'interno del metodo `onCreateView`): 

      ```kotlin
      val checkNuovoModulo = linear.findViewById<CheckBox>(R.id.checkNuovoModulo)
      val nuovoModulo = sharedPref?.getBoolean(MOD_NUOVO_MODULO, true)
      checkNuovoModulo.isChecked = nuovoModulo!!
      ```

   3. modificare il `savePref.setOnClickListener{...}` aggiungendo la nuova preferenza: 

      ```kotlin
      editor?.putBoolean(MOD_NUOVO_MODULO, checkNuovoModulo.isChecked)
      (activity as MainActivity?)?.setDrawerElementVisible(R.id.itemNuovoModulo, checkNuovoModulo.isChecked)
      ```

##### Si può poi aggiungere una scheda nella schermata di login in un istante:

Ho predisposto due tipi di "carte" nella schermata di Login, singolo oggetto ma con due costruttori: uno con solo titolo, l'altro con titolo, descrizione, colore del titolo e id dell'immagine che si vuole impostare. Il codice si trova all'interno di _com.modolo.healthyplus/_**LoginFragment.kt** e consiste nell'aggiungere:

- ```kotlin
  cardList.add(LoginCard("Nuovo Modulo"))
  ```

  oppure

- ```kotlin
  cardList.add(LoginCard(
  "Nuovo Modulo",
  "Descrizione nuovo modulo",
  ContextCompat.getColor(requireContext(), R.color.main_nuovomodulo),
  R.drawable.cardimg_nuovomodulo)
  )
  ```

Nel primo caso avremo una carta con titolo "Nuovo Modulo" nero e immagine stock, nel secondo una carta con lo stesso titolo, però impostato del colore "main_nuovomodulo", una descrizione e "cardimg_nuovomodulo" come immagine.

-------

In ogni caso se si aprono i file sopra descritti si troveranno le righe di codice qui presenti, commentate.
