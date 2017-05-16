package log;

public enum Priority {
	
	CRITICAL, //erreur critique, qui doit être traitée immédiatement
	HIGH, //erreur de haute priorité
	NORMAL, //erreur de priorité normale
	LOW, //erreur non bloquante
	INFO, //information systeme
	NOTIFICATION, //message de notification (d'un événement par exemple)
	
}
