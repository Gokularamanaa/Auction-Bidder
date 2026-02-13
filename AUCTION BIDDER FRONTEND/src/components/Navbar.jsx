import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Container } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import { logout } from '../api/authService';

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const isActive = (path) => {
        return location.pathname === path ? 'rgba(255,255,255,0.2)' : 'transparent';
    };

    const roles = JSON.parse(localStorage.getItem('roles') || '[]');
    const isAdmin = roles.includes('ROLE_ADMIN');

    return (
        <AppBar position="sticky" elevation={0} sx={{ bgcolor: 'rgba(255, 255, 255, 0.8)', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(0,0,0,0.05)' }}>
            <Container maxWidth="lg">
                <Toolbar disableGutters sx={{ height: 70 }}>
                    <Typography
                        variant="h5"
                        noWrap
                        component="div"
                        sx={{
                            flexGrow: 1,
                            display: 'flex',
                            alignItems: 'center',
                            fontWeight: 800,
                            cursor: 'pointer',
                            color: 'primary.main',
                            letterSpacing: '-0.03em'
                        }}
                        onClick={() => navigate('/dashboard')}
                    >
                        AuctionBidder
                    </Typography>

                    <Box sx={{ display: 'flex', gap: 1 }}>
                        <Button
                            onClick={() => navigate('/dashboard')}
                            sx={{
                                color: location.pathname === '/dashboard' ? 'primary.main' : 'text.secondary',
                                bgcolor: location.pathname === '/dashboard' ? 'primary.50' : 'transparent',
                                px: 2,
                                borderRadius: 3
                            }}
                        >
                            Dashboard
                        </Button>
                        <Button
                            onClick={() => navigate('/auctions')}
                            sx={{
                                color: location.pathname === '/auctions' ? 'primary.main' : 'text.secondary',
                                bgcolor: location.pathname === '/auctions' ? 'primary.50' : 'transparent',
                                px: 2,
                                borderRadius: 3
                            }}
                        >
                            Explore
                        </Button>
                        {isAdmin && (
                            <Button
                                onClick={() => navigate('/create-auction')}
                                sx={{
                                    color: location.pathname === '/create-auction' ? 'primary.main' : 'text.secondary',
                                    bgcolor: location.pathname === '/create-auction' ? 'primary.50' : 'transparent',
                                    px: 2,
                                    borderRadius: 3
                                }}
                            >
                                <span style={{ marginRight: 4 }}>+</span> Create
                            </Button>
                        )}
                        <Button
                            onClick={handleLogout}
                            sx={{ color: 'text.secondary', px: 2, borderRadius: 3 }}
                        >
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
};

export default Navbar;
