

88""Yb 88""Yb 888888    db    88  dP 888888 88  88 88""Yb  dP"Yb  88   88  dP""b8 88  88
88__dP 88__dP 88__     dPYb   88odP    88   88  88 88__dP dP   Yb 88   88 dP   `" 88  88
88""Yb 88"Yb  88""    dP__Yb  88"Yb    88   888888 88"Yb  Yb   dP Y8   8P Yb  "88 888888
88oodP 88  Yb 888888 dP""""Yb 88  Yb   88   88  88 88  Yb  YbodP  `YbodP'  YboodP 88  88



               ein Spiel im Rahmen des allgemeinen Programmierpraktikums 
                       an der Fakultaet fuer Informatik im SS2016



                                    by CodeSalat:
                                 
                                Fabiola Buschendorf
                                    A. Z.
                                    H. A.
                                    J. B.

                              Betreuer: J. R.



SPIEL & SPIELREGELN
-------------------

Die Idee des Spiels besteht darin, die Reihen des Gegners zu durchbrechen, bevor dieser 
das bei den eigenen tut. Hierzu besitzt jeder Spieler auf einem Spielbrett, welches zwi-
schen 2 und 26  Feldern breit und zwischen 6 und 26 Feldern hoch ist, Spielfiguren. 
Diese sind entweder rot oder blau. Ihre Anzahl richtet sich nach der Geometrie des Bret-
tes. 

Jeder Spielstein kann nur ein Feld vorwärts bewegt werden. Erlaubt sind dabei Züge nach 
vorne links, ein Feld gerade auf den Gegner zu oder ein Feld nach vorne rechts. Es her-
rscht zugzwang. Gegnerische Spielsteine können geschlagen werden, wenn sie der nächste 
Stein auf einer Diagonalen eines eigenen Steins liegen. Schlagen gilt als ein Zug und 
der eigene Stein nimmt dann die Position des geschlagen Steins an. Es dürfen keine Stei-
ne übersprungen werden. 

Ein Spieler gewinnt das Spiel, wenn er es schafft die gegnersiche Grundlinie zu erreich-
en oder alle Steine des Gegners auszulöschen. Hierbei ist es unerheblich ob dies durch 
einen Zug oder das Schlagen eines gegnerischen Steins geschieht.


ABHAENGIGKEITEN
---------------

- ant 1.9
- javac
- java

ENTPACKEN DES ARCHIVES
-----------------------

Unter Linux kann das gelieferte Archiv wie folgt entpackt werden:

	$ tar -xf breakthroughppCodeSalat.tar 

Unter Windows kann dies mit einem geeigneten Tool erfolgen.

Der nächste Schritt besteht in der Kompilation des Projekts.


KOMPILIEREN
-----------

Um das Spiel selbst zu kompilieren kann die Buildumgebung ant verwendet werden:

	$ ant

Dies generiert einen Ordner "dest" in dem ausfuehrbare jar Dateien "breakthrough.jar"
und "breakthrough-net.jar" abgelegt wurden.



SPIEL STARTEN
-------------

Mit dem Befehl

	$ java -jar dest/breakthrough.jar

kann das einfache Spiel mit 2 interaktiven Spielern ohne graphische Ausgabe gestartet werden.

Um hinter einer Firewall ueber ein geeignetes Netzwerk oder gegen Computergegner zu spielen kann der Befehl

	$ java -jar dest/breakthrough-net.jar 

verwendet werden.

Fuer die verschiedenen Optionen und Kommandozeilenparameter siehe unten.



KOMMANDOZEILENPARAMETER breakthrough.jar
----------------------------------------

Kombinierbare optionale Einstellungen:
-d							Anzeigen des Spielbretts und zusätzlicher Informationen zum Ablauf des
							Spiels. Die Standardeinstellung ist aus.
-s <Zeilen> <Spalten>		Definiert die Spielfeldgröße. Die Standardeinstellung ist die maximale
							Feldgröße 26 26
-l <Dateiname>			Lädt einen vorher gespeicherten Spielstand mit zwei interaktiven
							Spielern, welche in der Konsole auf einem Brett mit der gespeicherten
							Spielfeldgröße spielen
-c                   Farbige Ausgabe des Spielbrettes auf der Konsole



KOMMANDOZEILENPARAMETER breakthrough-net.jar
--------------------------------------------

Kombinierbare optionale Einstellungen:
-d							Anzeigen des Spielbretts und zusätzlicher Informationen zum Ablauf des
							Spiels. Die Standardeinstellung ist aus.
-s <Zeilen> <Spalten>		Definiert die Spielfeldgröße. Die Standardeinstellung ist die maximale
							Feldgröße 26 26
-m IP|SC|AC|AI IP|SC|AC|AI	Wahl der Arten der Spieler die gegeneinander spielen sollen: 
									IP: interaktiver Spieler
									SC: Computerspieler der mit Zufall spielt
									AC: etwas besserer Computerspieler
									AI: noch besserer Comuterspieler
							Standardwahl: IP IP
-b <int> <int>				Definiert die Art der Ausgabe und ggf. der Eingabe für die mit option
							"-m" gewählten Computerspieler:
								0:	IP:			Standard, Ein- und Ausgabe im Terminal
									SC|AC|AI:	Standard, keine Ausgabe									
								1:	IP:			Ein- und Ausgabe mithilfe eines GUI											
									SC|AC|AI:	Ausgabe im Terminal, oder im GUI wenn der -w Schalter gesetzt ist. 
-w <ms>						Nur erlaubte option zusammen mit -m SC|AC|AI SC|AC|AI. Die Zahl definiert 
							die Zeit, die zu einem Zug hinzuaddiert wird, um einen gegnerischen Zug
							zu verzögern, Standardwert ist 0 ms
 								mit -n local: Beide Spieler werden in einer GUI dargestellt
 								mit -n offer|receive (<IP>): Zeigt separate GUI der verschiedenen
										Farben auf den beteiligten Rechnern
-l <Dateiname>				Lädt einen vorher gespeicherten Spielstand mit zwei interaktiven
							Spielern, welche in der Konsole auf einem Brett mit der gespeicherten
							Spielfeldgröße spielen. Erlaubt nur mit "-n local"
-n offer|receive (<IP>)|local	Erlaubt das Spielen hinter einer Firewall in einem geeigneten
								Netzwerk:
 								offer:	Bietet einen Spieler auf dem aktuellen Computer an (guest).
										Er akzeptiert die durch den anbietenden Computer
										festgesetzte Größe des Spielfeldes. Der offerierende Spieler 
										ist rot. Der Gegner bestimmt die Spielfeldgröße.
 								receive (<IP>): Sucht einen Spieler bei einer <IP> und managed das
									daraus resultierende Spiel (host). Wenn <IP> nicht angegeben
									wird, wird der Spieler im Fortgang zur Eingabe der selbigen
									aufgefordert. Ist das Spiel zuende kann der receive-spieler eine
									neue Anfrage senden, ohne das ein neues Angebot gesendet werden muss.
 								local: Standardoption für lokales Spielen
								Die angebotenen bzw. die Spieler, die mit von anderen Computern 
								angebotenen Spielern zusammen spielen, werden durch die erste 
								Option von "-m" beschrieben
-t <int>					Ungefähre Zeit, die ein Computer mit "-n receive (<IP>)" auf einen 
							anderen Spieler bei der IP warted, bevor ein Fehler entsteht. Standard-
							einstellung ist 10 s
-c                          Farbige Ausgabe des Spielbrettes auf der Konsole

Kombinationen die zu Fehlern führen:
-l <fileName> und -n offer
-l <fileName> und -n receive
-w <int> und -m IP IP
-w <int> und -m SC|AC|AI IP
-w <int> und -m IP SC|AC|AI


Ein Beispiel für das Spielen über Netzwerk:
	Erster Spieler:

	$ java -jar dest/breakthrough-net.jar -n offer 

	Zweiter Spieler (starten mit GUI):

	$ java -jar dest/breakthrough-net.jar -n receive <IP> -s X X -b 1 1


DOKUMENTATION
-------------

Die Dokumentation wird automatisch im Ordner "docs" abgelegt wenn das ganze Projekt mit

    $ ant

gebaut wird. Wenn nicht das ganze Projekt sondern nur die Dokumentation generiert werden soll kann das mit

    $ ant javadoc

gemacht werden.

Um die Dokumentation zu oeffnen muss die Datei "docs/index.html" mit einem Browser geoffnet werden:

    $ firefox docs/index.html &
    $ google-chrome docs/index.html &

