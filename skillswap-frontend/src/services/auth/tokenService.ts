import storageService from "../../utils/storage";

class TokenService {
  getAccessToken(): string | null {
    return storageService.getAccessToken();
  }

  setAccessToken(token: string): void {
    storageService.setAccessToken(token);
  }

  removeAccessToken(): void {
    storageService.removeAccessToken();
  }

  hasAccessToken(): boolean {
    return Boolean(this.getAccessToken());
  }

  clear(): void {
    storageService.clear();
  }
}

const tokenService = new TokenService();

export default tokenService;