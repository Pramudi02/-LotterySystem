// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged
} from "firebase/auth";
import {
  getFirestore,
  doc,
  setDoc,
  getDoc,
  updateDoc,
  collection,
  query,
  where,
  getDocs,
  addDoc,
  orderBy,
  limit
} from "firebase/firestore";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBWEtzjum1mcPN3e61XghXaNC9qum7vhDw",
  authDomain: "lotterysystem-18963.firebaseapp.com",
  projectId: "lotterysystem-18963",
  storageBucket: "lotterysystem-18963.firebasestorage.app",
  messagingSenderId: "41637006495",
  appId: "1:41637006495:web:e7c9e6e3a57792bbc126aa",
  measurementId: "G-HKLLXJNVHS"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);
const db = getFirestore(app);

// Authentication functions
export const firebaseAuth = {
  // Sign up new user
  async signUp(email, password, displayName) {
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      // Create user document in Firestore
      await setDoc(doc(db, "users", userCredential.user.uid), {
        email: email,
        displayName: displayName,
        balance: 100.0,
        createdAt: new Date(),
        ticketIds: []
      });
      return { success: true, user: userCredential.user };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Sign in existing user
  async signIn(email, password) {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      return { success: true, user: userCredential.user };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Sign out
  async signOut() {
    try {
      await signOut(auth);
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Get current user
  getCurrentUser() {
    return auth.currentUser;
  },

  // Listen to auth state changes
  onAuthStateChange(callback) {
    return onAuthStateChanged(auth, callback);
  }
};

// Firestore database functions
export const firebaseDB = {
  // Get user data
  async getUserData(userId) {
    try {
      const userDoc = await getDoc(doc(db, "users", userId));
      if (userDoc.exists()) {
        return { success: true, data: userDoc.data() };
      } else {
        return { success: false, error: "User not found" };
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Update user balance
  async updateUserBalance(userId, newBalance) {
    try {
      await updateDoc(doc(db, "users", userId), {
        balance: newBalance
      });
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Buy ticket
  async buyTicket(userId) {
    try {
      // Generate 5 random numbers
      const numbers = [];
      for (let i = 0; i < 5; i++) {
        numbers.push(Math.floor(Math.random() * 100) + 1);
      }

      // Create ticket document
      const ticketData = {
        userId: userId,
        numbers: numbers,
        purchaseTime: new Date(),
        ticketId: Date.now() // Simple ID generation
      };

      const docRef = await addDoc(collection(db, "tickets"), ticketData);

      // Update user's ticket list and balance
      const userDoc = await getDoc(doc(db, "users", userId));
      if (userDoc.exists()) {
        const userData = userDoc.data();
        const currentBalance = userData.balance || 0;
        if (currentBalance < 10) {
          return { success: false, error: "Insufficient balance" };
        }

        const newBalance = currentBalance - 10;
        const ticketIds = userData.ticketIds || [];
        ticketIds.push(docRef.id);

        await updateDoc(doc(db, "users", userId), {
          balance: newBalance,
          ticketIds: ticketIds
        });

        return { success: true, numbers: numbers, balance: newBalance };
      } else {
        return { success: false, error: "User not found" };
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Get user tickets
  async getUserTickets(userId) {
    try {
      const q = query(
        collection(db, "tickets"),
        where("userId", "==", userId),
        orderBy("purchaseTime", "desc")
      );
      const querySnapshot = await getDocs(q);

      const tickets = [];
      querySnapshot.forEach((doc) => {
        const ticketData = doc.data();
        tickets.push({
          id: doc.id,
          numbers: ticketData.numbers,
          purchaseTime: ticketData.purchaseTime.toDate(),
          won: false, // Will be calculated based on winning number
          prize: 0
        });
      });

      return { success: true, tickets: tickets };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Set winning number (admin only)
  async setWinningNumber(number) {
    try {
      await setDoc(doc(db, "system", "lottery"), {
        winningNumber: number,
        setTime: new Date()
      }, { merge: true });
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Get winning number
  async getWinningNumber() {
    try {
      const docSnap = await getDoc(doc(db, "system", "lottery"));
      if (docSnap.exists()) {
        return { success: true, winningNumber: docSnap.data().winningNumber || 0 };
      } else {
        return { success: true, winningNumber: 0 };
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Get all tickets (admin only)
  async getAllTickets() {
    try {
      const querySnapshot = await getDocs(collection(db, "tickets"));
      const tickets = [];
      querySnapshot.forEach((doc) => {
        const ticketData = doc.data();
        tickets.push({
          id: doc.id,
          userId: ticketData.userId,
          numbers: ticketData.numbers,
          purchaseTime: ticketData.purchaseTime.toDate()
        });
      });
      return { success: true, tickets: tickets };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Get all users (admin only)
  async getAllUsers() {
    try {
      const querySnapshot = await getDocs(collection(db, "users"));
      const users = [];
      querySnapshot.forEach((doc) => {
        const userData = doc.data();
        users.push({
          id: doc.id,
          email: userData.email,
          displayName: userData.displayName,
          balance: userData.balance,
          ticketCount: userData.ticketIds?.length || 0
        });
      });
      return { success: true, users: users };
    } catch (error) {
      return { success: false, error: error.message };
    }
  }
};

export { auth, db };