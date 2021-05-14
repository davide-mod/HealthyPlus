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
