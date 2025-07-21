import React, { useEffect, useState, useRef } from 'react';
import CryptoTable from '../components/CryptoTable';
import '../styles/form.css';
import '../styles/home-page.css';

const BATCH_SIZE = 50;
const MAX_SUBSCRIPTIONS = 20;

const fetchAllUsdPairsWithVolume = async () => {
    try {
        const assetPairsRes = await fetch(
            'https://api.kraken.com/0/public/AssetPairs'
        );
        const assetPairsData = await assetPairsRes.json();
        const usdPairs = Object.entries(assetPairsData.result)
            .filter(([_, pair]) => pair.quote === 'ZUSD' && pair.wsname)
            .map(([key, pair]) => ({
                wsname: pair.wsname,
                base: pair.base,
                altname: pair.altname,
                key: key,
            }));

        let allTickerData = {};
        for (let i = 0; i < usdPairs.length; i += BATCH_SIZE) {
            const batch = usdPairs.slice(i, i + BATCH_SIZE);
            const batchKeys = batch.map((p) => p.key);
            try {
                const tickerRes = await fetch(
                    `https://api.kraken.com/0/public/Ticker?pair=${batchKeys.join(
                        ','
                    )}`
                );
                const tickerData = await tickerRes.json();
                Object.assign(allTickerData, tickerData.result);
            } catch (err) {
                for (const key of batchKeys) {
                    try {
                        const singleRes = await fetch(
                            `https://api.kraken.com/0/public/Ticker?pair=${key}`
                        );
                        const singleData = await singleRes.json();
                        Object.assign(allTickerData, singleData.result);
                    } catch (singleErr) {
                        console.error(
                            `Failed to fetch ticker for ${key}:`,
                            singleErr
                        );
                    }
                }
            }
        }

        const baseVolumes = {};
        usdPairs.forEach((pair) => {
            const ticker = allTickerData[pair.key];
            if (!ticker) return;
            const baseVolume = parseFloat(ticker.v ? ticker.v[0] : 0);
            const avgPrice = parseFloat(ticker.p ? ticker.p[1] : 0);
            const usdValue = baseVolume * avgPrice;
            if (!baseVolumes[pair.base]) {
                baseVolumes[pair.base] = [];
            }
            baseVolumes[pair.base].push({
                wsname: pair.wsname,
                base: pair.base,
                usdValue,
            });
        });

        const aggregated = Object.entries(baseVolumes).map(([base, pairs]) => {
            const totalUsdValue = pairs.reduce((sum, p) => sum + p.usdValue, 0);
            const bestPair = pairs.reduce((a, b) =>
                a.usdValue > b.usdValue ? a : b
            );
            return {
                base,
                wsname: bestPair.wsname,
                usdValue: totalUsdValue,
            };
        });

        return aggregated.sort((a, b) => b.usdValue - a.usdValue);
    } catch (err) {
        console.error('Error fetching top currencies:', err);
        return [];
    }
};

const HomePage = () => {
    const [prices, setPrices] = useState({});
    const [topPairsInfo, setTopPairsInfo] = useState([]);
    const wsRef = useRef(null);
    const allPairsRef = useRef([]);
    const nextPairIndexRef = useRef(0);

    const subscribeToNextAvailablePairs = (count) => {
        const ws = wsRef.current;
        if (!ws || ws.readyState !== WebSocket.OPEN) return;

        const pairsToSubscribe = [];
        while (
            pairsToSubscribe.length < count &&
            nextPairIndexRef.current < allPairsRef.current.length
        ) {
            const pair = allPairsRef.current[nextPairIndexRef.current];
            pairsToSubscribe.push(pair);
            nextPairIndexRef.current++;
        }

        if (pairsToSubscribe.length > 0) {
            const symbols = pairsToSubscribe.map((p) => p.wsname);
            ws.send(
                JSON.stringify({
                    method: 'subscribe',
                    params: { channel: 'ticker', symbol: symbols },
                })
            );
            console.log(`Attempting to subscribe to: ${symbols.join(', ')}`);
            setTopPairsInfo((prevPairs) => [...prevPairs, ...pairsToSubscribe]);
        }
    };

    useEffect(() => {
        let isMounted = true;

        const connect = () => {
            const ws = new WebSocket('wss://ws.kraken.com/v2');
            wsRef.current = ws;

            ws.onopen = () => {
                console.log('WebSocket connected');
                subscribeToNextAvailablePairs(MAX_SUBSCRIPTIONS);
            };

            ws.onmessage = (event) => {
                const data = JSON.parse(event.data);

                if (
                    data.channel === 'ticker' &&
                    (data.type === 'snapshot' || data.type === 'update') &&
                    Array.isArray(data.data)
                ) {
                    setPrices((prev) => {
                        const updated = { ...prev };
                        data.data.forEach((item) => {
                            if (item.symbol && item.last) {
                                updated[item.symbol] = item.last;
                            }
                        });
                        return updated;
                    });
                } else if (data.error) {
                    console.error(
                        `Subscription error for ${data.symbol}: ${data.error}`
                    );
                    setTopPairsInfo((prevPairs) =>
                        prevPairs.filter((p) => p.wsname !== data.symbol)
                    );
                    subscribeToNextAvailablePairs(1);
                }
            };

            ws.onerror = (err) => {
                console.error('WebSocket error:', err);
            };

            ws.onclose = () => {
                console.log('WebSocket disconnected. Reconnecting...');
                if (isMounted) {
                    setTimeout(connect, 5000);
                }
            };
        };

        fetchAllUsdPairsWithVolume().then((allPairs) => {
            if (isMounted) {
                allPairsRef.current = allPairs;
                connect();
            }
        });

        return () => {
            isMounted = false;
            if (wsRef.current) {
                wsRef.current.close();
            }
        };
    }, []);

    const handleTrade = (type, pair, amount) => {
        console.log(`${type} request:`, {
            pair: pair.wsname,
            price: prices[pair.wsname],
        });
    };

    return (
        <div className="form-bg">
            <div className="home-page-card">
                <h1 className="home-page-title">
                    Welcome to Crypto Trading Sim!
                </h1>
                <p className="home-page-subtitle">
                    You are logged in. Start trading or explore the app.
                </p>
                <h2 className="home-page-table-title">
                    Top 20 Crypto Prices (Live from Kraken)
                </h2>
                <CryptoTable
                    topPairsInfo={topPairsInfo}
                    prices={prices}
                    handleTrade={handleTrade}
                />
            </div>
        </div>
    );
};

export default HomePage;
