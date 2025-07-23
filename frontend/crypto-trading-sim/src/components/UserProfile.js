import React from 'react';
import './../styles/user-profile.css';

const UserProfile = ({ user, onWalletReset }) => {
    if (!user) {
        return null;
    }

    const handleReset = () => {
        if (window.confirm('Are you sure you want to reset your wallet? This action is irreversible.')) {
            onWalletReset();
        }
    };

    return (
        <div className="user-profile-card">
            <h2 className="user-profile-title">User Profile</h2>
            <p><strong>Username:</strong> {user.username}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <div className="user-profile-balance">
                <h3>Balance: ${user.wallet.balance.toFixed(2)}</h3>
            </div>
            <div className="user-profile-assets">
                <h3>Your Assets</h3>
                <ul>
                    {user.wallet.assets.map(asset => (
                        <li key={asset.symbol}>
                            <span>{asset.symbol}</span>
                            <span>{asset.amount}</span>
                        </li>
                    ))}
                </ul>
            </div>
            <button onClick={handleReset} className="reset-wallet-btn">
                Reset Wallet
            </button>
        </div>
    );
};

export default UserProfile;
