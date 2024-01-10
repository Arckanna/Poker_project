package com.ivray.poker.business;
import java.time.LocalDate;
import java.util.Objects;


public class Player {
	
		private String pseudo;
		private LocalDate birthDay;
		private float balance;
		private Town playerTown;
		
		public Player(String pseudo) {
			super();
			this.pseudo = pseudo;
		}
		public Player(String pseudo, LocalDate birthDay) {
			this(pseudo);		
			this.birthDay = birthDay;
		}
		public Player(String pseudo, LocalDate birthDay, float balance) {
			this(pseudo,birthDay);		
			this.balance = balance;
		}
		public Player(String pseudo, LocalDate birthDay, float balance, Town playerTown) {
			this(pseudo,birthDay,balance);
			this.playerTown = playerTown;
		}
		
		public String getPseudo() {
			return pseudo;
		}
		public void setPseudo(String pseudo) {
			this.pseudo = pseudo;
		}
		
		public LocalDate getBirthDay() {
			return birthDay;
		}
		public void setBirthDay(LocalDate birthDay) {
			this.birthDay = birthDay;
		}
		
		public float getBalance() {
			return balance;
		}
		public void setBalance(float balance) {
			this.balance = balance;
		}
		
		public Town getPlayerTown() {
			return playerTown;
		}
		public void setPlayerTown(Town playerTown) {
			this.playerTown = playerTown;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(balance, birthDay, playerTown, pseudo);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Player other = (Player) obj;
			return Float.floatToIntBits(balance) == Float.floatToIntBits(other.balance)
					&& Objects.equals(birthDay, other.birthDay) && Objects.equals(playerTown, other.playerTown)
					&& Objects.equals(pseudo, other.pseudo);
		}
		
		@Override
		public String toString() {
			return "Player [pseudo=" + pseudo + ", birthDay=" + birthDay + ", balance=" + balance + ", playerTown="
					+ playerTown + "]";
		}	
		
}
