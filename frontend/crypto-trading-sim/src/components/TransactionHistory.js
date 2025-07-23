import React from 'react';
import './../styles/crypto-table.css';

const TransactionHistory = ({ transactions }) => {
    if (!transactions || transactions.length === 0) {
        return <p>You have no transactions yet.</p>;
    }

    return (
        <table className="crypto-table">
            <thead>
                <tr>
                    <th>Type</th>
                    <th>Asset</th>
                    <th>Amount</th>
                    <th>Price</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                {transactions.map((transaction) => (
                    <tr key={transaction.id}>
                        <td>{transaction.type}</td>
                        <td>{transaction.symbol}</td>
                        <td>{transaction.amount}</td>
                        <td>{transaction.price}</td>
                        <td>{new Date(transaction.createdAt).toLocaleString('en-US', {
                            year: '2-digit',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false
                        })}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default TransactionHistory;
