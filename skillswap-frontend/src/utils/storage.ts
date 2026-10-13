
const ACCESS_TOKEN_KEY = "skillswap_access_token";
const USER_KEY = "skillswap_user";

class StorageService {
  setAccessToken(token: string): void {
    localStorage.setItem(
      ACCESS_TOKEN_KEY,
      token
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem(
      ACCESS_TOKEN_KEY
    );
  }

  removeAccessToken(): void {
    localStorage.removeItem(
      ACCESS_TOKEN_KEY
    );
  }

  setUser<T>(user: T): void {
    localStorage.setItem(
      USER_KEY,
      JSON.stringify(user)
    );
  }

  getUser<T>(): T | null {
    const user =
      localStorage.getItem(USER_KEY);

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user) as T;
    } catch {
      return null;
    }
  }

  removeUser(): void {
    localStorage.removeItem(USER_KEY);
  }

  clear(): void {
    localStorage.removeItem(
      ACCESS_TOKEN_KEY
    );

    localStorage.removeItem(
      USER_KEY
    );
  }

  isAuthenticated(): boolean {
    return Boolean(
      this.getAccessToken()
    );
  }
}

const storageService =
  new StorageService();

export default storageService;

