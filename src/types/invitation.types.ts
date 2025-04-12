export interface Invitation {
    id: number;
    group: {
        id: number;
        name: string;
    };
    inviter: {
        id: number;
        username: string;
    };
    invitee: {
        id: number;
        username: string;
    };
    status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
    createdAt: string;
    respondedAt?: string;
} 