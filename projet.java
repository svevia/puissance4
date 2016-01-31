import extensions.*;
class projet extends Program{



class Bouton{
	TransparentImage img;
	int x;
	int y;
}

class Compte{
	String login;
	int totaleQuestion;
	int questionsReussi;
}

    CSVFile question = loadCSV("question.csv",';');
    CSVFile stat = loadCSV("stat.csv",';');
    String[][] classement= new String [rowCount(stat)][columnCount(stat)];
    char[][] grille = new char[6][7];/* grille de jeu qui est un tableau de caractères de 6 lignes et 7 colonnes
    									Ce tableau peut contenir 3 types de caractères:
    									R:un jeton de couleur "rouge"(joueur 2)
    									J:un jeton de couleur "jaune"(joueur 1)
    									' ':l'absence de jetons*/


    int positionPointeur = 0;/* cette variable permet au joueur de communiquer avec le programmes tout
    							au long du jeu. Dans les menu elle permet au joueur de choisir une proposition
    							en indiquant la ligne actuellement sélectionner.
    							En jeu elle indique la colonne que le joueur sélectionne (soit à la souris ou au clavier)
    							l'IA utilise également cette variable pour positionner son jeton.*/
    
    int joueur = 0;
    Compte compte1 = new Compte();
    Compte compte2 = new Compte();
    int mode = 0;// mode choisi par le joueur : 1 mode 1 joueur, 2 mode 2 joueur
    String[] tabReponse;
    int indicequestion =0;
    int[] tableaudoublon = new int[rowCount(question)];
    int menu = 0; /*etat du joueur dans la partie : 0 demande de login
    												1 sur le menu de présenation
													2 en jeu
													3 question
													4 victoire ou égalité
													5 vu de la grille après une victoire ou égalité
													6 verification resultat reponse:
														61 reponse fausse
														62 reponse vraie
													7 classement*/



    //==============Images utilisés pour l'interface du jeu=============//
	TransparentImage jeton0 = newTransparentImage("img/jaune.png");
	TransparentImage jeton1 = newTransparentImage("img/rouge.png");
	TransparentImage jetonJauneDernier = newTransparentImage("img/jaune_dernier.png");
	TransparentImage jetonRougeDernier = newTransparentImage("img/rouge_dernier.png");
	TransparentImage jetonActuel;
	TransparentImage curseur = newTransparentImage("img/curseur.png");
	TransparentImage pip = newTransparentImage("fond", 672, 688);
	TransparentImage fond = newTransparentImage("img/grille.png");
	TransparentImage pelemele = newTransparentImage("img/pele_mele.png");
	//==================================================================//


	Bouton[] bouton = new Bouton[4];


    void algorithm(){
//======================initialisation de l'interface=========================//
			attributionBouton();
			remplirTableauStat();
			entreePseudo();
			 show(pip);
			initialisationGrille();
			readString();
}
//===========================================================================//

	void remplirTableauStat(){
		for(int ligne = 0;ligne < length(classement,1);ligne++){
			for(int colonne = 0;colonne < length(classement,2);colonne++){
				classement[ligne][colonne] = getCell(stat, ligne, colonne);
			}
		}
	}


	void affichageClassement(){//classe les joueurs par ordre de taux de réussite et affiche le résultat
		TransparentImage retour = newTransparentImage("retour","img/retour.png");
		fill(pip, rgbColor(0, 42, 224));//bleu
		setNewFont(pip,"TOP 10", "BOLD", 45);
		setColor(pip,RGBColor.WHITE );
		drawImage(pip,retour,10,10);
		drawString(pip, "TOP 10", 250,50);
		int max =0;

		int[][] ratioReussite = new int[length(classement,1)][2];
		for(int i =0;i<length(ratioReussite);i++){
			ratioReussite[i][0]=(int)(Float.parseFloat(classement[i][2])/Float.parseFloat(classement[i][1])*100);
			ratioReussite[i][1]=i;
		}
		ratioReussite = triDecroissant(ratioReussite);

		if (length(classement,1)>10){max = 10;}
		else{max = length(classement,1)-1;}
		setNewFont(pip,"classement", "BOLD", 18);
		for(int i =0;i<=max;i++){
			if(equals(classement[ratioReussite[i][1]][0],compte1.login)){
				setColor(pip,rgbColor(0, 153, 0) );//jaune
			}
			else{
				setColor(pip,rgbColor(119, 136, 153) );//gris
			}
			fillRect(pip, 100, 90+60*i, 450, 40);//rectange de contour de chaque pseudo
			setColor(pip,RGBColor.WHITE );
			drawString(pip, classement[ratioReussite[i][1]][0], 120,110+60*i);//login du joueur
			drawString(pip, ratioReussite[i][0] + "%", 300,110+60*i);//pourcentage de réussite du joueur
			drawString(pip, "sur " + classement[ratioReussite[i][1]][1] + " questions", 380,110+60*i);//pourcentage de réussite du joueur
		}
	}

	int[][] triDecroissant(int[][] tab){
		int modification = 0;
		int[] temp;
		for(int i = 0;i<length(tab)-1;i++){
			if (tab[i][0]<tab[i+1][0]){
				temp = tab[i];
				tab[i] = tab[i+1];
				tab[i+1] = temp;
				modification += 1;
			}
		}
		if(modification>=1){
			return triDecroissant(tab);
		}
		else{return tab;}


	}


	void testTriDecroissant(){

		int[][] tab = new int[5][1];
		for(int i = 0;i<length(tab,1);i++){
				tab[i][0] = (int)(random()*10);
		}
		tab = triDecroissant(tab);

		 assertGreaterThanOrEqual(tab[2][0],tab[1][0]);
	}

	void entreePseudo(){
		fill(pip, rgbColor(0, 42, 224));//bleu
		setColor(pip,RGBColor.WHITE );

    	setNewFont(pip,"presentation", "BOLD", 30);
    	drawString(pip, "Entres ton nom en bas!", 150,250);
    	setMessage(pip, "Quel est ton nom ?");
    	drawImage(pip,pelemele,0,450);
	}

	void entreePseudo2(){
		fill(pip, rgbColor(0, 42, 224));//bleu
		setColor(pip,RGBColor.WHITE );

    	setNewFont(pip,"presentation", "BOLD", 30);
    	drawString(pip, "Joueur 2 entres ton nom en bas!", 100,250);
    	setMessage(pip, "Quel est ton nom ?");
    	drawImage(pip,pelemele,0,450);
    }

	void attributionBouton(){
	for(int i = 0;i<4;i++){
    	bouton[i] = new Bouton();
    	bouton[i].img = newTransparentImage(Integer.toString(i+1),"img/bouton.png");
		}
    bouton[0].x = 100;
    bouton[0].y = 420;
    bouton[1].x = 350;
    bouton[1].y = 420;
    bouton[2].x = 100;
    bouton[2].y = 520;
    bouton[3].x = 350;
    bouton[3].y = 520;
	}

//===================Fonction gestion de l'interface=======================//
	void initialisationGrille(){ // initialisa la grille en mettant toute les valeurs à ""
	for(int i = 0;i<length(grille,1);i++){
		for(int y =0;y<length(grille,2);y++){
		    grille[i][y] =' ' ;}}

	}

	void initialisationAffichage(){// crée le fond et le curseur au dessus de la grille
    fill(pip, RGBColor.BLACK); 
    jetonJoueur();
 	drawImage(pip,fond,0,100);
 	drawImage(pip,jetonActuel,10,0);
	}


	void rafraichissement(){//fonction utilisée après qu'une question ai été posé, la grille est alors vidé puis les jetons sont
							//repositionné au bon endroit
    fill(pip, RGBColor.BLACK); 

	for(int ligne = 0;ligne<length(grille,1);ligne++){
		for(int colonne =0;colonne<length(grille,2);colonne++){
			if(grille[ligne][colonne]=='J'){drawImage(pip,jeton0,5 + 96*colonne,(getHeight(jeton0)+15)+95*ligne);}
			if (grille[ligne][colonne]=='R'){drawImage(pip,jeton1,5 + 96*colonne,(getHeight(jeton1)+15)+95*ligne);}
		}
	}


    drawImage(pip,fond,0,100);
 	menu = 2;
	}

	void poseJeton(int ligne){//pose un jeton et recolle la grille par dessus.

		if(joueur == 0){
			drawImage(pip,jetonJauneDernier,5 + 96*positionPointeur,(getHeight(jetonJauneDernier)+15)+95*ligne);
		}
		else{
			drawImage(pip,jetonRougeDernier,5 + 96*positionPointeur,(getHeight(jetonRougeDernier)+15)+95*ligne);
		}
		
		drawImage(pip,fond,0,100);

	}


   void presentation(){
    		  setColor(pip,rgbColor(119, 136, 153) );//gris
    		  fillRect(pip, 80, 200, 500, 350);
    		  setColor(pip,RGBColor.BLACK );

    		  setNewFont(pip,"presentation", "BOLD", 30);
    		  drawString(pip, "1 joueur", 200,250);
    		  drawString(pip, "2 joueurs", 200,330);
    		  drawString(pip, "Classement", 200,410);
    		  drawString(pip, "Quitter", 200,490);
    		  drawImage(pip,curseur,100,220+80*positionPointeur);

}

    void affichageStop(){
    	setColor(pip,rgbColor(119, 136, 153) );//gris
    	fillRect(pip, 80, 200, 500, 350);
    	setColor(pip,RGBColor.BLACK );
    	setNewFont(pip,"presentation", "BOLD", 30);

    	if(verifBlocage()){
    	drawString(pip, "Egalité !", 200,250);}

    	else{drawString(pip, "le joueur "+ ((1-joueur)+1) + " a gagné !", 200,250);}

    	drawString(pip, "Voir la grille ?", 200,350);
    	drawString(pip, "Rejouer ?", 200,400);
    	drawString(pip, "Retour ?", 200,450);
    	drawString(pip, "Quitter ?", 200,500);
    	drawImage(pip,curseur,100,320+50*positionPointeur);


    }
//=============================================================================//

	int positionnement(){
		int ligne = 5; //5 est la ligne la plus basse du tableau, et 0 est la plus haute
		if (positionPointeur >= 0){
	    	while(ligne >= 0 && grille[ligne][positionPointeur]!=' '){
				ligne = ligne -1;}
	    		if (ligne >= 0){
					if (joueur == 0){
						grille[ligne][positionPointeur] = 'J';
					}
					else{
						grille[ligne][positionPointeur] = 'R';
						}
					}
				}
				return ligne;
			}




	void jetonJoueur(){ //fonction remplaçant le jeton au dessus de la grille indiquant où le joueur veut placer son jetons
						//Ce jeton change de couleur en fonction du joueur
		if(joueur == 0){
			jetonActuel = jeton0;
		} else {
			jetonActuel = jeton1;
		}
	drawImage(pip,jetonActuel,10+97*positionPointeur,0);
	}
		

    boolean verifBlocage(){ //verifie si la grille est pleine
    	for(int i = 0; i<=6;i++){
    		if(grille[0][i] == ' '){
    			return false;
    		}
    	}
    	return true;
    }

    boolean verifVictoire(int ligne){
	int compteurJeton = 0;

	/*Pour chaque cas on prend la ligne, puis la colonne et les 2 diagonales et l'on vérifie si 4 jetons sont identique
	à celui qui vient d'être posé par l'utilisateur*/

		/*Verification de la ligne où le joueur a posé son jeton*/
	for(int i =0;i <length(grille,2);i++)
		{
		    if(grille[ligne][i] == grille[ligne][positionPointeur]){
			compteurJeton = compteurJeton + 1;
			if(compteurJeton == 4){return true;} //dès que 4 jetons consécutifs sont trouvés, on renvoi vrai : il y a victoire
		    }
		    else{compteurJeton = 0;}
		}


		/*Verification de la colonne où le joueur a posé son jeton*/
	compteurJeton = 0;
	for (int i = 0;i<length(grille,1);i++){
	    if(grille[i][positionPointeur] == grille[ligne][positionPointeur]){
		compteurJeton = compteurJeton + 1;
		if(compteurJeton == 4){return true;}//dès que 4 jetons consécutifs sont trouvés, on renvoi vrai : il y a victoire
		    }
		    else{compteurJeton = 0;}
	}
    

		/*Verification diagonale descendante qui part de en haut à droite*/
	compteurJeton = 0;
	int ligneDiagoDroite = ligne;
	int colonneDiagoDroite = positionPointeur;
	while(ligneDiagoDroite>0 && colonneDiagoDroite<length(grille,2)-1){//remonte la diagonale jusqu'au départ
		ligneDiagoDroite -=1;
		colonneDiagoDroite += 1;
	}
	while (ligneDiagoDroite < length(grille,1) && colonneDiagoDroite >= 0){//compte chaque case de la diagonale et verifie si 4 jetons alignés
		if(grille[ligneDiagoDroite][colonneDiagoDroite] == grille[ligne][positionPointeur]){
			compteurJeton = compteurJeton + 1;
			if(compteurJeton == 4){return true;}//dès que 4 jetons consécutifs sont trouvés, on renvoi vrai : il y a victoire
		}
		else{compteurJeton = 0;} // si jeton d'une autre couleur trouvé, on remet le compteur à 0
		ligneDiagoDroite = ligneDiagoDroite + 1;
		colonneDiagoDroite = colonneDiagoDroite - 1;
	}



		/*Verification diagonale descendante qui part de en haut à gauche*/
	compteurJeton = 0;
	int ligneDiagoGauche = ligne;
	int colonneDiagoGauche = positionPointeur;
	while(ligneDiagoGauche>0 && colonneDiagoGauche>0){//remonte la diagonale vers le haut gauche de la grille
		ligneDiagoGauche -=1;
		colonneDiagoGauche -= 1;
	}

	while (ligneDiagoGauche < length(grille,1) && colonneDiagoGauche < length(grille,2)){ //compte chaque case de la diagonale et verifie si 4 jetons alignés
		if(grille[ligneDiagoGauche][colonneDiagoGauche] == grille[ligne][positionPointeur]){
			compteurJeton = compteurJeton + 1;
			if(compteurJeton == 4){return true;} //dès que 4 jetons consécutifs sont trouvés, on renvoi vrai : il y a victoire
		}
		else{compteurJeton = 0;}
		ligneDiagoGauche = ligneDiagoGauche + 1;
		colonneDiagoGauche = colonneDiagoGauche + 1;
	}

	return false; //Si toute les verifications sont effectué et qu'aucune victoire n'a été trouvé, on renvoi faux
    }
    
//============================Fonction gestion evenement=============================//


	void keyChanged(char c, String event){
		setColor(pip,RGBColor.BLACK );;
		if(c==ANSI_RIGHT && event =="PRESSED" && menu == 2){
			if (positionPointeur<6){
				fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
				positionPointeur += 1;
				jetonJoueur();
			}
		}
						
		if(c==ANSI_LEFT && event =="PRESSED" && menu == 2){
			if (positionPointeur> 0){
				fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
				positionPointeur -= 1;
				jetonJoueur();
			}
		}

		if((c==ANSI_DOWN || c==' '|| c=='\n') && event =="PRESSED" && menu == 2){
		if(grille[0][positionPointeur] == ' '){ //vérifie si la colonne où le joueur veut placer son jeton n'est pas pleine.
			menu = 3;
			printquestion();
			affichagequestion(tabReponse);
			}
		}


		if(c==ANSI_DOWN && event =="PRESSED" && menu == 1){
			if(positionPointeur < 3){
				positionPointeur += 1;
				presentation();
			}
	}

		if(c==ANSI_UP && event =="PRESSED" && menu == 1){
			if(positionPointeur > 0){
				positionPointeur -= 1;
				presentation();
			}
	}


		if((c==' '|| c=='\n') && event =="PRESSED" && menu == 1){
			if(positionPointeur == 0){ //mode 1 joueur
				positionPointeur = 0;
				initialisationAffichage();
				menu = 2;
				mode = 1;

			}
			if(positionPointeur == 1){ //mode 2 joueur
				positionPointeur = 0;
				if(compte2.login == null){
					menu = 0;
					entreePseudo2();
				}
				else{
					initialisationAffichage();
					menu = 2;
					mode = 2;
				}
			}
			if(positionPointeur == 2){
				positionPointeur = 0;
				menu = 7;
				affichageClassement();
			}

			if(positionPointeur == 3){
				hide(pip);
			}
	}

		if(c==ANSI_DOWN && event =="PRESSED" && menu == 4){
			if(positionPointeur < 3){
				positionPointeur += 1;
				affichageStop();
			}
	}

		if(c==ANSI_UP && event =="PRESSED" && menu == 4){
			if(positionPointeur > 0){
				positionPointeur -= 1;
				affichageStop();
			}
	}

		if((c==' ' || c=='\n') && event =="PRESSED" && menu == 4){
			if(positionPointeur == 0){//voir la grille
				rafraichissement();
				menu = 5;
				event = "";
			}
			if(positionPointeur == 1){//rejouer
				if(mode == 1){joueur = 0;}
				positionPointeur = 0;
				initialisationAffichage();
				initialisationGrille();
				menu = 2;
			}
			if(positionPointeur == 2){//retour au menu principal
				menu = 1;
				positionPointeur = 0;
				joueur = 0;
				initialisationGrille();
				initialisationAffichage(); //affiche la grille et le pointeur
				presentation();
			}
			if(positionPointeur == 3){
				hide(pip);
			}
	}

		if(menu == 5 && event == "PRESSED"){
			affichageStop();
			menu = 4;}

		if((int)(menu/10)==6){
			if(menu == 61 && event == "PRESSED"){
				rafraichissement();
				joueur = 1 - joueur;
				jetonJoueur();
				menu = 2;
			}

			if(menu == 62 && event == "PRESSED"){
				rafraichissement();
				int ligne = positionnement();
				if(ligne >= 0){ // vérifie si il y a une place dans la colonne
					poseJeton(ligne);
					joueur = 1 - joueur;
					fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
					jetonJoueur();
					menu = 2;
					if(verifVictoire(ligne) || verifBlocage()){
						sauvegarder();
						fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
						positionPointeur = 0;
						jetonJoueur();
						menu = 4;
						affichageStop();
					}
				}
			}
			if(mode == 1 && joueur == 1 && menu == 2){//appel de l'IA après affichage des réponses aux questions
				appelIA();	
			}
		}
	}
	void mouseHasMoved(int x, int y){
		if(menu == 2){
			setColor(pip,RGBColor.BLACK );
			fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
			positionPointeur = (x/100);
			jetonJoueur();
		}


	}
	void mouseChanged(String name, int x, int y, int button, String event){

		if(menu == 7 && event == "CLICKED" && name == "retour"){
			menu = 1;
			initialisationAffichage(); //affiche la grille et le pointeur
			presentation();
		}

		if(menu == 3 && event == "CLICKED" && name != "DEFAULT"){
			event = "";
			bonnereponse(name);}
		if(menu == 2 && event == "CLICKED"){
			event = "";
			if(grille[0][positionPointeur] == ' '){ //vérifie si la colonne où le joueur veut placer son jeton n'est pas pleine.
				printquestion();
				menu = 3;
				affichagequestion(tabReponse);
				}
			}
		
		if(menu == 5 && event == "CLICKED"){
			affichageStop();
			menu = 4;
		}

	}

	void mouseIsDragged(int x, int y, int button, int clickCount){}

	void textEntered(String text){
		if(menu == 0){
			if(compteExistant(text) == -1){
				creerCompte(text);
				chargerCompte(text);
			}
			else{
				chargerCompte(text);
			}
		}
		setMessage(pip, "Saisie");
		setFocus(pip,true);
		if(menu == 0){
			if(compte2.login == null){//si c'est le debut du jeu, 1 seul compte initialisé
				menu = 1;
				initialisationAffichage(); //affiche la grille et le pointeur
				presentation();

			}
			else{//si le deuxième compte a été initialisé, le joueur a choisi le mode 2 joueurs
				initialisationAffichage();
				menu = 2;
				mode = 2;
			}
		}

	}

//======================================================================//
//=========================Fonctions du classement======================//


	void chargerCompte(String login){
		int idx = compteExistant(login);
		if (compte1.login == null){
			compte1.login = login;
			compte1.totaleQuestion = Integer.parseInt(classement[idx][1]);
			compte1.questionsReussi = Integer.parseInt(classement[idx][2]);
		}
		else if(compte2.login == null){
			compte2.login = login;
			compte2.totaleQuestion = Integer.parseInt(classement[idx][1]);
			compte2.questionsReussi = Integer.parseInt(classement[idx][2]);
		}
	}

	int compteExistant(String login){
		for(int i = 0;i<length(classement,1);i++){
			if(equals(classement[i][0],login)){
				return i;
			}
		}
		return -1;
	}


	void creerCompte(String login){
				String[][] temp = new String[length(classement,1)+1][length(classement,2)];
				for(int i = 0;i<length(classement,1);i++){
					for(int j = 0;j<length(classement,2);j++){
						temp[i][j]=classement[i][j];
					}
				}
				temp[length(temp,1)-1][0] = login;
				for(int i = 1;i<length(temp,2);i++){
					temp[length(temp,1)-1][i] = "0";
				}
				classement = new String[length(temp,1)][length(temp,2)];
				for(int i = 0;i<length(classement,1);i++){
					for(int j = 0;j<length(classement,2);j++){
						classement[i][j] = temp[i][j];
						
					}
				}
			}


	void sauvegarder(){
		int idx = compteExistant(compte1.login);
		classement[idx][1]=Integer.toString(compte1.totaleQuestion);
		classement[idx][2]=Integer.toString(compte1.questionsReussi);

		if(compte2.login != null){
			idx = compteExistant(compte2.login);
			classement[idx][1]=Integer.toString(compte2.totaleQuestion);
			classement[idx][2]=Integer.toString(compte2.questionsReussi);
		}
		saveCSV(classement, "stat.csv", ';');
	}



//============================Fonctions du CSV===========================//

    void printquestion() {
    //met les reponses dans un tableau

    tabReponse = new String[7];	
    int nmbligne = rowCount(question);
	int doublon = 0;
	if (indicequestion==nmbligne) {
		indicequestion=0;
	}
	if(indicequestion==0){
	for(int i=0; i<length(tableaudoublon); i++){
	    tableaudoublon[i]=i;
	}
	for(int y=0; y<length(tableaudoublon); y++){
	    doublon =(int)(random()*rowCount(question));
	    int temp = tableaudoublon[doublon];
	    tableaudoublon[doublon]= tableaudoublon[y];
	    tableaudoublon[y]=temp;
	}}
	for (int column=0; column<columnCount(question, indicequestion); column++) {
			tabReponse[column]= getCell(question, tableaudoublon[indicequestion], column);
		}
		indicequestion = indicequestion + 1;
	}



	String[] couperQuestion(String texte){//Coupe une question si elle est trop grande pour rentrer d'une seule ligne dans l'interface
		String[] question = new String[2];
    	question[0] = texte;
    	question[1] = "";
    	if(length(texte) > 35){
    		for(int i = 35;i<length(texte);i--){
    			if(charAt(texte,i) == ' ' && i < length(texte)-1){
    				question[0] = substring(texte,0,i);
    				question[1] = substring(texte,i+1,length(texte));
    				if(length(question[1]) <= 2){
    					question[0] = question[0] + question[1];
    					question[1] = "";
    					}
    				return question;
    			}
    		}

    	}

    return question;
	}


	void testCouperQuestion(){
		assertArrayEquals(couperQuestion("La question"),new String[]{"La question",""});
		assertArrayEquals(couperQuestion("Une question de plus de 35 caractères sera coupée"),
			new String[]{"Une question de plus de 35","caractères sera coupée"});
	}





		void affichagequestion(String[] tabReponse){
		TransparentImage fond_question = newTransparentImage("img/fond-question.png");

		drawImage(pip,fond_question,0,100);
    	setNewFont(pip,"presentation", "BOLD", 25);
    	setColor(pip,rgbColor(255,255,255) );

		String[] question = couperQuestion(tabReponse[0]);//Verifie si la question doit être coupé pour être affiché sur 2 lignes

    	drawString(pip,question[0], 90,250);
    	drawString(pip,question[1], 90,280);


    	for(int i = 0;i<4;i++){
    		drawImage(pip,bouton[i].img,bouton[i].x,bouton[i].y);
    	}

		
		drawString(pip,tabReponse[1], 113 ,465);
		drawString(pip,tabReponse[2], 363 ,465);
		drawString(pip,tabReponse[3], 113 ,565);
		drawString(pip,tabReponse[4],363,565);


	}



	void changementBouton(int bonneReponse, int reponseJoueur){//afiche des boutons différent après réponses du joueur pour la bonne réponse
		TransparentImage bvrai_r = newTransparentImage("img/bouton-vrai-relache.png");
		TransparentImage bfaux = newTransparentImage("img/bouton-faux.png");
		TransparentImage bvrai_e = newTransparentImage("img/bouton-vrai-enfonce.png");
		TransparentImage fond_question = newTransparentImage("img/fond-question.png");

		drawImage(pip,fond_question,0,100);
    	setNewFont(pip,"presentation", "BOLD", 25);
    	setColor(pip,rgbColor(255,255,255) );
		jetonJoueur();
		String[] question = couperQuestion(tabReponse[0]);//Verifie si la question doit être coupé pour être affiché sur 2 lignes

    	drawString(pip,question[0], 90,250);
    	drawString(pip,question[1], 90,280);

		for(int i = 0;i<4;i++){//repositionne tous les boutons sauf la bonne reponse et celle du joueur
			if(bonneReponse != i+1 || reponseJoueur != i+1){
    			drawImage(pip,bouton[i].img,bouton[i].x,bouton[i].y);
    		}
    	}
    	if(bonneReponse == reponseJoueur){
    		drawImage(pip,bvrai_e,bouton[bonneReponse-1].x,bouton[bonneReponse-1].y);
    	}
    	else{
			drawImage(pip,bvrai_r,bouton[bonneReponse-1].x,bouton[bonneReponse-1].y);
			drawImage(pip,bfaux,bouton[reponseJoueur-1].x,bouton[reponseJoueur-1].y);
		}
		
		drawString(pip,tabReponse[1], 113 ,465);
		drawString(pip,tabReponse[2], 363 ,465);
		drawString(pip,tabReponse[3], 113 ,565);
		drawString(pip,tabReponse[4], 363 ,565);
	}

    void bonnereponse(String reponsejoueur){
	    rafraichissement();
	   	if (equals(reponsejoueur,tabReponse[5])){ //si la reponse est correct, ajout du jeton et poursuite du jeu
	   			if(joueur == 0){
					compte1.totaleQuestion += 1;
					compte1.questionsReussi += 1;
				}
				else if (joueur == 1){
					compte2.totaleQuestion += 1;
					compte2.questionsReussi += 1;
				}

	   			menu = 62;
				changementBouton(Integer.parseInt(tabReponse[5]),Integer.parseInt(reponsejoueur));

				}
		else{ // si mauvaise réponse, le jeton n'est pas posé et c'est le tour du joueur suivant.
			   	if(joueur == 0){
					compte1.totaleQuestion += 1;
				}
				else if (joueur == 1){
					compte2.totaleQuestion += 1;
				}
				
			menu = 61;
			changementBouton(Integer.parseInt(tabReponse[5]),Integer.parseInt(reponsejoueur));
			}
		

    }

//================================================================================//
//================================Fonction de l'IA================================//



    class Alignement{
    	String type;
    	int[] debut;
    	int[] fin;
    	int[] espace;
    	char couleur;
    	int taille = 0;
    }

    void appelIA(){
    	rafraichissement();
		menu = 7; // pour empêcher toutes actions de l'utilisateur pendant le calcul de l'IA
		positionPointeur = calculColonne(3);
		int ligneIA = positionnement();
		poseJeton(ligneIA);
		joueur = 1-joueur;
		jetonJoueur();
		menu = 2;
		if(verifVictoire(ligneIA) || verifBlocage()){
			sauvegarder();
			fillRect(pip,10+97*positionPointeur,0,getWidth(jetonActuel),getHeight(jetonActuel));
			positionPointeur = 0;
			jetonJoueur();
			menu = 4;
			affichageStop();
			}
    }

	int calculColonne(int nombreRecherche){ // parcourt de tout le tableau pour trouver la colonne optimal pour le positionnement de l'IA
		Alignement suite = new Alignement();
//-------------------Parcourt de toutes les lignes de la grille------------------------//
		for (int i =length(grille,1)-1;i>=0;i--){
			suite.type = "ligne";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;
			for(int j =length(grille,2)-1;j>= 0;j--){
			
				suite = parcourtGrille(suite,i,j);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = i;
					suite.fin[1] = j;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}
				}
			}		
		}

//-------------------Parcourt de toutes les colonnes de la grille------------------------//
		for (int i =length(grille,2)-1;i>=0;i--){
			suite.type = "colonne";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;
			for(int j =length(grille,1)-1;j>=0;j--){

				suite = parcourtGrille(suite,j,i);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = j;
					suite.fin[1] = i;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}
				}

			}
		}



//--------------------Parcourt des diagonales de haut gauche vers bas droite------------------------//
		for(int ligne = 2;ligne>=0;ligne--){
			suite.type = "Diagonale";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;

			int i = ligne;
			int j = 0;
			while(i<length(grille,1) && j<length(grille,2)){
				suite = parcourtGrille(suite,i,j);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = i;
					suite.fin[1] = j;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}

				}
				i += 1;
				j += 1;
			}
		}


		for(int colonne = 3;colonne>=0;colonne--){
			suite.type = "Diagonale";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;

			int i = 0;
			int j = colonne;
			while(i<length(grille,1) && j<length(grille,2)){
				suite = parcourtGrille(suite,i,j);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = i;
					suite.fin[1] = j;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}

				}
				i += 1;
				j += 1;
			}
		}





//--------------------Parcourt des diagonales de haut droite vers bas gauche------------------------//
		for(int ligne = 2;ligne>=0;ligne--){
			suite.type = "Diagonale";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;

			int i = ligne;
			int j = 6;
			while(i<length(grille,1) && j>=0){
				suite = parcourtGrille(suite,i,j);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = i;
					suite.fin[1] = j;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}

				}
				i += 1;
				j -= 1;
			}
		}


		for(int colonne = 6;colonne>=3;colonne--){
			suite.type = "Diagonale";
			suite.debut = new int[]{-1,-1};
			suite.espace = new int[]{-1,-1};
			suite.fin = new int[]{-1,-1};
			suite.taille = 0;

			int i = 0;
			int j = colonne;
			while(i<length(grille,1) && j>=0){
				suite = parcourtGrille(suite,i,j);

				if(suite.taille >= nombreRecherche){
					suite.fin[0] = i;
					suite.fin[1] = j;
					int colonneAction = rechercheColonneAction(suite);
					if (colonneAction != -1){
						return colonneAction;
					}

				}
				i += 1;
				j -= 1;
			}
		}



		if(nombreRecherche == 3){
			return calculColonne(2); //si aucun alignement de 3 jetons n'a permis un positionnement
		}							 //on reparcourt la grille pour 2 jetons alignés
		else{return positionnementAleatoire();}
	}



		int positionnementAleatoire(){
			int colonneAlea = (int) (random()*6);
			if(grille[0][colonneAlea]==' '){
					return colonneAlea;
				}
			else{return positionnementAleatoire();}
			}


		Alignement parcourtGrille(Alignement suite, int ligne, int colonne){

			if(grille[ligne][colonne] == 'R'){
				if(suite.debut[0] == -1 || suite.couleur == 'J'){
					suite.debut[0] = ligne;
					suite.debut[1] = colonne;
					suite.couleur = 'R';
					suite.espace[0] = -1;
					suite.espace[1] = -1;
					suite.taille = 1;
				}
				else{suite.taille += 1;}

			}
			else if(grille[ligne][colonne] == 'J'){
				if(suite.debut[0] == -1 || suite.couleur == 'R'){
					suite.debut[0] = ligne;
					suite.debut[1] = colonne;
					suite.couleur = 'J';
					suite.espace[0] = -1;
					suite.espace[1] = -1;
					suite.taille = 1;
				}
				else{suite.taille +=1;}
			}
			else{	
				if(suite.espace[0] == -1){
					suite.espace[0] = ligne;
					suite.espace[1] = colonne;
				}
				else{ // 2 espaces successif, remise à zero des compteurs
					suite.espace[0] = -1;
					suite.espace[1] = -1;
					suite.debut[0] = -1;
					suite.debut[1] = -1;
					suite.taille = 0;
				}
			}
			return suite;
		}


		int rechercheColonneAction(Alignement suite){ //fonction appelé quand 3 jetons sont détéctés
			if(suite.type == "colonne" && suite.fin[0]>0){ /*si 3 jeton sont détéctés en colonne alors la seule case
															possible est au dessus, on vérifie qu'elle soit libre*/
				if(grille[suite.fin[0]-1][suite.fin[1]] == ' '){
					return suite.fin[1];//si une place est disponible au dessus des 3 jetons on renvoi cette colonne
				}
			}


			else if(suite.type == "ligne"){ //si 3 jetons sont détéctés en ligne

				if(suite.fin[0]!=5){//si l'on est pas en bas de la grille (pour éviter les out of bounds)

					if(suite.espace[0] != -1 && grille[suite.espace[0]+1][suite.espace[1]] != ' '){
						/*si il y a un espace au milieu de la série, on vérifie si la case d'en dessous est prise,
						si oui on renvoi la colonne où est situé l'espace*/
						return suite.espace[1];
					}

					if(suite.debut[1] != 6){//si la suite n'est pas collé au bord droit

						//OutOfBunds a -1 ligne d'en dessous


						if(grille[suite.debut[0]][suite.debut[1]+1] == ' ' && grille[suite.debut[0]+1][suite.debut[1]+1] != ' '){
							/*on vérifie si le bord droit de la suite est libre et si il y a bien un jeton à la ligne
							d'en desous*/
							return suite.debut[1]+1;// la case à droite de la fin de la serie
						}
					}

					if(suite.fin[1] != 0){//si la suite n'est pas collé au bord gauche
						if(grille[suite.fin[0]][suite.fin[1]-1] == ' ' && grille[suite.fin[0]+1][suite.fin[1]-1] != ' '){
							//on vérifie sie l bord gauche de la suite est libre et si il y a un jeton à la ligne d'en dessous
							return suite.fin[1]-1;//la case à gauche de la série
						}
					}
				}
				else{//si la serie est sur la dernière ligne du tableau
					if(suite.espace[0] != -1){//pas besoin de vérifier si la case sous l'espace est prise
						return suite.espace[1];
					}

					if(suite.debut[1] != 6){
						if(grille[suite.debut[0]][suite.debut[1]+1]== ' '){//si le bord droit est libre
							return suite.debut[1]+1;
						}
					}

					if(suite.fin[1] != 0){
						if(grille[suite.fin[0]][suite.fin[1]-1] == ' '){//si le bord gauche est libre
							return suite.fin[1]-1;
						}
					}

				}
			}


			else if(suite.type == "Diagonale"){//si 3 jetons sont détéctés en diagonale
				if(suite.fin[0]!=5){//si l'on est pas en bas de la grille (pour éviter les out of bounds)

					if(suite.espace[0] != -1 && grille[suite.espace[0]+1][suite.espace[1]] != ' '){
						/*si il y a un espace au milieu de la série, on vérifie si la case d'en dessous est prise,
						si oui on renvoi la colonne où est situé l'espace*/
						return suite.espace[1];
					}
				}


				int decalageColonne = 0;//sens de la diagonale
				if(suite.fin[1]-suite.debut[1] > 0){decalageColonne = 1;}//diagonale de droite à gauche
				else{decalageColonne = -1;}//diagonale de gauche à droite


				if(suite.fin[0] < 4 && suite.fin[1]+decalageColonne <= 6 && suite.fin[1]+decalageColonne >= 0){
				//verifie que la fin de la série n'est pas dans un coin
					if(grille[suite.fin[0]+1][suite.fin[1]+decalageColonne] == ' ' && grille[suite.fin[0]+2][suite.fin[1]+decalageColonne] != ' '){
						//vérifie que la case suivante de la diagonale est vide
						return suite.fin[1]+decalageColonne;
					}
				}
				else if(suite.fin[0] < 5 && suite.fin[1]+decalageColonne <= 6 && suite.fin[1]+decalageColonne >= 0){
				//si la case où l'on veut poser le jeton est sur la dernière ligne, il ne faut pas vérifier en dessous
					if(grille[suite.fin[0]+1][suite.fin[1]+decalageColonne] == ' '){
						//vérifie que la case suivante de la diagonale est vide
						return suite.fin[1]+decalageColonne;
					}
				}


				if(suite.debut[0] > 0 && suite.debut[1]-decalageColonne >= 0 && suite.debut[1]-decalageColonne <= 6){
					//verifie que le début de la série n'est pas dans un coin
					if(grille[suite.debut[0]-1][suite.debut[1]-decalageColonne] == ' ' && grille[suite.debut[0]][suite.debut[1]-decalageColonne] != ' '){
					//vérifie que la case suivante de la diagonale est vide
					//decalageColonne -1 car nous voulons aller à la case avant le début donc on remonte la diagonale
						return suite.debut[1]-decalageColonne;
					}
				}
			}
			return -1; // si aucune possiblité trouvée avec cette série on retourne -1
		}

}