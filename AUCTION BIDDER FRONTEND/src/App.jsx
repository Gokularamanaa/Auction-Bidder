import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme, CssBaseline, Box } from '@mui/material';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import AuctionList from './pages/AuctionList';
import AuctionDetails from './pages/AuctionDetails';
import CreateAuction from './pages/CreateAuction';
import Navbar from './components/Navbar';

const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#8b5cf6', // Electric Violet
            light: '#a78bfa',
            dark: '#7c3aed',
        },
        secondary: {
            main: '#ec4899', // Hot Pink
            light: '#f472b6',
            dark: '#db2777',
        },
        background: {
            default: 'transparent',
            paper: '#ffffff', // Solid white for maximum readability
        },
        text: {
            primary: '#000000', // Pure black for max contrast
            secondary: '#334155', // Darker slate
        },
        divider: 'rgba(0, 0, 0, 0.12)',
    },
    typography: {
        fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif',
        h1: { fontWeight: 800, letterSpacing: '-0.025em', color: '#1e293b' },
        h2: { fontWeight: 700, letterSpacing: '-0.025em', color: '#1e293b' },
        h3: { fontWeight: 700, letterSpacing: '-0.025em', color: '#1e293b' },
        h4: { fontWeight: 700, letterSpacing: '-0.025em' },
        h5: { fontWeight: 600 },
        h6: { fontWeight: 600 },
        button: { textTransform: 'none', fontWeight: 700 },
        body1: { fontWeight: 500, lineHeight: 1.7 }, // Increased weight
        body2: { fontWeight: 500, lineHeight: 1.7 },
    },
    shape: {
        borderRadius: 16, // Fun, rounded shapes
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    padding: '10px 24px',
                    boxShadow: '0 4px 6px -1px rgba(139, 92, 246, 0.2)', // Purple shadow
                    '&:hover': {
                        transform: 'translateY(-2px)',
                        boxShadow: '0 10px 15px -3px rgba(139, 92, 246, 0.3)',
                    },
                },
                contained: {
                    background: 'linear-gradient(45deg, #8b5cf6 30%, #ec4899 90%)', // Gradient buttons
                }
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backdropFilter: 'blur(10px)',
                    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03)',
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: 16,
                    border: '1px solid rgba(255, 255, 255, 0.5)',
                    background: 'rgba(255, 255, 255, 0.8)',
                }
            }
        },
    },
});



const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('token');
    // console.log("ProtectedRoute check. Token present:", !!token); // Cleaned up log
    if (!token) {
        return <Navigate to="/" replace />;
    }
    return (
        <>
            <Navbar />
            <Box sx={{ py: 4 }}>
                {children}
            </Box>
        </>
    );
};

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <Dashboard />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/auctions"
                        element={
                            <ProtectedRoute>
                                <AuctionList />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/auctions/:id"
                        element={
                            <ProtectedRoute>
                                <AuctionDetails />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/create-auction"
                        element={
                            <ProtectedRoute>
                                <CreateAuction />
                            </ProtectedRoute>
                        }
                    />
                </Routes>
            </BrowserRouter>
        </ThemeProvider>
    );
}

export default App;
