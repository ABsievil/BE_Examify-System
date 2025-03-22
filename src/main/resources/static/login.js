document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form');
    
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const rememberMe = document.getElementById('remember-me').checked;
        
        // Gửi yêu cầu đăng nhập
        fetch('/api/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
                rememberMe: rememberMe
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'OK') {
                // Lưu JWT token vào cookie
                const token = data.data;
                const expirationDays = rememberMe ? 7 : 1; // 7 ngày nếu "Ghi nhớ đăng nhập", nếu không thì 1 ngày
                
                // Thiết lập cookie với JWT token
                const expirationDate = new Date();
                expirationDate.setDate(expirationDate.getDate() + expirationDays);
                document.cookie = `jwt_token=${token}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;
                
                // Chuyển hướng đến trang chủ
                window.location.href = '/home';
            } else {
                // Hiển thị thông báo lỗi
                const errorDiv = document.createElement('div');
                errorDiv.className = 'alert alert-danger';
                errorDiv.textContent = data.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
                
                // Xóa thông báo lỗi cũ nếu có
                const existingAlert = document.querySelector('.alert');
                if (existingAlert) {
                    existingAlert.remove();
                }
                
                // Thêm thông báo lỗi vào đầu form
                loginForm.insertAdjacentElement('beforebegin', errorDiv);
            }
        })
        .catch(error => {
            console.error('Lỗi đăng nhập:', error);
            
            // Hiển thị thông báo lỗi
            const errorDiv = document.createElement('div');
            errorDiv.className = 'alert alert-danger';
            errorDiv.textContent = 'Đã xảy ra lỗi khi kết nối đến máy chủ. Vui lòng thử lại sau.';
            
            // Xóa thông báo lỗi cũ nếu có
            const existingAlert = document.querySelector('.alert');
            if (existingAlert) {
                existingAlert.remove();
            }
            
            // Thêm thông báo lỗi vào đầu form
            loginForm.insertAdjacentElement('beforebegin', errorDiv);
        });
    });
});
