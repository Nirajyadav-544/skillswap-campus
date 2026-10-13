import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";

import storageService from "../utils/storage";

export interface AuthUser {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role?: string;
  emailVerified?: boolean;
  accountVerified?: boolean;
}

export interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  setAuth: (token: string, user: AuthUser) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedToken = storageService.getAccessToken();
    const storedUser = storageService.getUser<AuthUser>();

    setToken(storedToken);
    setUser(storedUser);
    setIsLoading(false);
  }, []);

  const setAuth = useCallback((newToken: string, newUser: AuthUser) => {
    storageService.setAccessToken(newToken);
    storageService.setUser(newUser);

    setToken(newToken);
    setUser(newUser);
  }, []);

  const logout = useCallback(() => {
    storageService.clear();

    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo<AuthContextType>(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token),
      isLoading,
      setAuth,
      logout,
    }),
    [user, token, isLoading, setAuth, logout]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};